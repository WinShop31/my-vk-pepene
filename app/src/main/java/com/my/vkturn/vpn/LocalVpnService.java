package com.my.vkturn.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import java.io.*;

public class LocalVpnService extends VpnService {
    private ParcelFileDescriptor vpnInterface = null;
    private Process coreProcess;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "START".equals(intent.getAction())) {
            String config = intent.getStringExtra("CONFIG");
            setupVpn();
            runCore(config);
        }
        return START_STICKY;
    }

    private void runCore(String config) {
        try {
            File coreFile = new File(getFilesDir(), "core");
            // Копируем из assets в память, если файла там еще нет
            if (!coreFile.exists()) {
                InputStream is = getAssets().open("core");
                FileOutputStream fos = new FileOutputStream(coreFile);
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) fos.write(buffer, 0, read);
                fos.close();
                is.close();
            }
            coreFile.setExecutable(true);

            // Запуск бинарника: передаем конфиг через аргументы
            ProcessBuilder pb = new ProcessBuilder(coreFile.getAbsolutePath(), "-c", config);
            // Перенаправляем логи в null, чтобы приложение не зависло от вывода
            pb.redirectErrorStream(true);
            coreProcess = pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVpn() {
        if (vpnInterface != null) return;
        Builder builder = new Builder();
        builder.addAddress("10.0.0.2", 24).addRoute("0.0.0.0", 0).addDnsServer("8.8.8.8");
        vpnInterface = builder.establish();
    }
    
    @Override
    public void onDestroy() {
        if (coreProcess != null) coreProcess.destroy();
        super.onDestroy();
    }
}
