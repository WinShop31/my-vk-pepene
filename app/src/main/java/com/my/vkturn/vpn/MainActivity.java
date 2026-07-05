package com.my.vkturn.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {
    private Button connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Создаем дизайн прямо в коде
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#121212"));

        TextView title = new TextView(this);
        title.setText("FPN: Твой протокол безопасности\n(VK TURN Edition)");
        title.setTextSize(22);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 80);

        connectBtn = new Button(this);
        connectBtn.setText("ПОДКЛЮЧИТЬ WDTT");
        connectBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        connectBtn.setTextColor(Color.WHITE);
        
        // По клику запрашиваем разрешение на VPN
        connectBtn.setOnClickListener(v -> startVpn());

        layout.addView(title);
        layout.addView(connectBtn);
        setContentView(layout);
    }

    private void startVpn() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            Intent startServiceIntent = new Intent(this, LocalVpnService.class);
            startServiceIntent.setAction("START");
            startService(startServiceIntent);
            connectBtn.setText("ТУННЕЛЬ ЗАПУЩЕН");
            connectBtn.setBackgroundColor(Color.parseColor("#F44336")); // Меняем цвет на красный
        }
    }
                }
