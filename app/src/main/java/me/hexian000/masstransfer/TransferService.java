package me.hexian000.masstransfer;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class TransferService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ("cancel".equals(intent.getAction())) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}

		Notification.Builder builder = new Notification.Builder(
				this.getApplicationContext());
		builder.setContentIntent(null)
				.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
						R.mipmap.ic_launcher))
				.setContentTitle("传输状态")
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentText("<文件名>")
				.setWhen(System.currentTimeMillis())
				.setProgress(100, 0, true)
				.setOngoing(true)
				.setVisibility(Notification.VISIBILITY_PUBLIC);

		PendingIntent cancelIntent;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Android 8.0+
			NotificationManager manager =
					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			if (manager != null) {
				NotificationChannel channel = new NotificationChannel(
						"transfer_state", "传输状态",
						NotificationManager.IMPORTANCE_DEFAULT
				);
				channel.enableLights(false);
				channel.enableVibration(false);
				channel.setSound(null, null);

				manager.createNotificationChannel(channel);
			}

			Intent cancel = new Intent(this, TransferService.class);
			cancel.setAction("cancel");
			cancelIntent = PendingIntent.getForegroundService(this, startId, cancel, 0);
		} else {
			// Android 7.1
			Intent cancel = new Intent(this, TransferService.class);
			cancel.setAction("cancel");
			cancelIntent = PendingIntent.getService(this, startId, cancel, 0);

			builder.setPriority(Notification.PRIORITY_DEFAULT)
					.setLights(0, 0, 0)
					.setVibrate(null)
					.setSound(null);
		}

		builder.addAction(new Notification.Action.Builder(
				null, getResources().getString(R.string.cancel), cancelIntent
		).build());
		Notification notification = builder.build();
		startForeground(startId, notification);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException();
	}
}
