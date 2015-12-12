package com.cyno.reminder.recievers;


import com.cyno.reminder_premium.service.PremiumAlarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmSetter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, PremiumAlarmService.class);
        service.setAction(PremiumAlarmService.CREATE);
        context.startService(service);
    
	}

}
