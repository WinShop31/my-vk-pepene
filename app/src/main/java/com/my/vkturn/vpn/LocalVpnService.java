package com.my.vkturn.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class LocalVpnService extends VpnService {
    private ParcelFileDescriptor vpnInterface = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "START".equals(intent.getAction())) {
            setupVpn();
        }
        return START_STICKY;
    }

    private void setupVpn() {
        if (vpnInterface != null) return;

        try {
            Builder builder = new Builder();
            // Настраиваем виртуальный IP внутри телефона
            builder.addAddress("10.0.0.2", 24);
            builder.addRoute("0.0.0.0", 0); // Перехватываем весь трафик
            builder.addDnsServer("8.8.8.8");
            builder.setSession("VK TURN Bypass");

            // Запускаем интерфейс
            vpnInterface = builder.establish();
            Log.i("WDTT", "Локальный туннель успешно создан!");
            
            // TODO: Здесь будет логика чтения трафика и упаковки в DTLS пакеты
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (vpnInterface != null) vpnInterface.close();
            vpnInterface = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
