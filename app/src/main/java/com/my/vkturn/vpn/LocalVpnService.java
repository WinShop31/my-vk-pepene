package com.my.vkturn.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class LocalVpnService extends VpnService {
    private ParcelFileDescriptor vpnInterface = null;

    // Переменные нашего конфига
    private String serverIp;
    private String dtlsPort;
    private String wgPort;
    private String localPort;
    private String password;
    private String vkHash;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "START".equals(intent.getAction())) {
            String configString = intent.getStringExtra("CONFIG");
            
            // Разбираем строку: IP:dtls_port:wg_port:local_port:password:vk_hash
            if (configString != null) {
                String[] parts = configString.split(":");
                if (parts.length >= 6) {
                    serverIp = parts[0];
                    dtlsPort = parts[1];
                    wgPort = parts[2];
                    localPort = parts[3];
                    password = parts[4];
                    vkHash = parts[5];
                    
                    Log.i("WDTT", "Успешный импорт конфига! Подключение к: " + serverIp + ":" + dtlsPort);
                    setupVpn();
                } else {
                    Log.e("WDTT", "Битый конфиг! Не хватает параметров.");
                }
            }
        }
        return START_STICKY;
    }

    private void setupVpn() {
        if (vpnInterface != null) return;

        try {
            Builder builder = new Builder();
            builder.addAddress("10.0.0.2", 24);
            builder.addRoute("0.0.0.0", 0);
            builder.addDnsServer("8.8.8.8");
            
            // Используем IP сервера для исключения из петли маршрутизации, чтобы VPN не замкнулся сам на себя
            // builder.addRoute(serverIp, 32); // (Раскомментируем при полной реализации сети)
            
            builder.setSession("VK TURN: " + serverIp);

            vpnInterface = builder.establish();
            Log.i("WDTT", "Интерфейс VPN поднят для сессии VK TURN.");
            
            // TODO: Запуск Go-бинарника или сетевого сокета для связи с сервером
            
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("WDTT", "Ошибка запуска туннеля: " + e.getMessage());
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
