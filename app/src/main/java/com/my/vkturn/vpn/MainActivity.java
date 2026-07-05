package com.my.vkturn.vpn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button connectBtn;
    private EditText configInput;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("WDTT_PREFS", Context.MODE_PRIVATE);

        // Дизайн интерфейса
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#121212"));
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("FPN: WDTT Tunnel");
        title.setTextSize(24);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 40);

        // Поле ввода для ссылки
        configInput = new EditText(this);
        configInput.setHint("Вставь ссылку wdtt://...");
        configInput.setHintTextColor(Color.GRAY);
        configInput.setTextColor(Color.WHITE);
        configInput.setTextSize(16);
        configInput.setPadding(20, 30, 20, 30);
        configInput.setBackgroundColor(Color.parseColor("#1E1E1E"));
        
        // Загружаем сохраненную ссылку, если она была
        String savedLink = prefs.getString("wdtt_link", "");
        configInput.setText(savedLink);

        // Отступ между полем и кнопкой
        TextView spacer = new TextView(this);
        spacer.setHeight(40);

        connectBtn = new Button(this);
        connectBtn.setText("ПОДКЛЮЧИТЬ");
        connectBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        connectBtn.setTextColor(Color.WHITE);
        
        connectBtn.setOnClickListener(v -> prepareConnection());

        layout.addView(title);
        layout.addView(configInput);
        layout.addView(spacer);
        layout.addView(connectBtn);
        
        setContentView(layout);
    }

    private void prepareConnection() {
        String link = configInput.getText().toString().trim();
        
        // Простая проверка формата
        if (!link.startsWith("wdtt://")) {
            Toast.makeText(this, "Ошибка: ссылка должна начинаться с wdtt://", Toast.LENGTH_LONG).show();
            return;
        }

        // Сохраняем ссылку, чтобы не вводить при следующем запуске
        prefs.edit().putString("wdtt_link", link).apply();
        
        // Запрашиваем права на VPN
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
            String link = configInput.getText().toString().trim();
            String configData = link.replace("wdtt://", ""); // Убираем префикс
            
            // Запускаем службу и передаем ей настройки
            Intent startServiceIntent = new Intent(this, LocalVpnService.class);
            startServiceIntent.setAction("START");
            startServiceIntent.putExtra("CONFIG", configData);
            startService(startServiceIntent);
            
            connectBtn.setText("ПОДКЛЮЧЕНО");
            connectBtn.setBackgroundColor(Color.parseColor("#F44336"));
        }
    }
}
