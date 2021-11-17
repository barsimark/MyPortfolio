package myapps.myportfolio.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import myapps.myportfolio.MainActivity
import myapps.myportfolio.R
import myapps.myportfolio.data.MyDatabase
import myapps.myportfolio.network.WebSQLBuilder
import myapps.myportfolio.network.WebShareMinimal
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class PriceUpdateWorker(context: Context, workerParameter: WorkerParameters) :
    Worker(context, workerParameter) {
    override fun doWork(): Result {
        val mediaType = "text/plain".toMediaTypeOrNull()
        val body = RequestBody.create(
            mediaType,
            WebSQLBuilder().SQLNamePriceQuery(MyDatabase.getInstance(applicationContext).assetDao().getAllShares())
        )
        val request = Request.Builder()
            .url("https://hotstoks-sql-finance.p.rapidapi.com/query")
            .post(body)
            .addHeader("content-type", "text/plain")
            .addHeader("x-rapidapi-host", "hotstoks-sql-finance.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "99dd94090emsha0710a563e8e79fp1194bcjsn29b8bb215d6e")
            .build()
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        val res = response.body?.string()

        var textToDisplay = ""
        if (res != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<WebShareMinimal>>() {}.type
            val jsonObject = JSONObject(res)
            val parsed: MutableList<WebShareMinimal> =
                gson.fromJson(jsonObject.getJSONArray("results").toString(), type)
            val shares = MyDatabase.getInstance(applicationContext).assetDao().getAllShares()
            for (parsedShare in parsed){
                val share = shares.find { it.name == parsedShare.symbol }
                if (share != null){
                    textToDisplay += String.format("%s: %.4f%%\n",
                        share.name, (parsedShare.price / share.price - 1.0) * 100.0)
                    share.price = parsedShare.price
                    MyDatabase.getInstance(applicationContext).assetDao().updateShare(share)
                }
            }

            sendNotification(textToDisplay)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(content: String){
        createNotificationChannel()
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val builder = NotificationCompat.Builder(applicationContext, "id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My Portfolio Notification")
            .setContentText("Update successful")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
    }
}