package stop.dizziness.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var btnToggle: MaterialButton
    private lateinit var etMargin: TextInputEditText
    private lateinit var sliderAlpha: Slider
    private lateinit var sliderSensitivity: Slider
    private lateinit var btnSelectColor: MaterialButton

    // Значение цвета по умолчанию — красный.
    private var chosenColor: Int = 0xFFFF0000.toInt()

    private var overlayEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Подключаем XML-разметку
        setContentView(R.layout.activity_main)

        btnToggle = findViewById(R.id.btnToggle)
        etMargin = findViewById(R.id.etMargin)
        sliderAlpha = findViewById(R.id.sliderAlpha)
        sliderSensitivity = findViewById(R.id.sliderSensitivity)
        btnSelectColor = findViewById(R.id.btnSelectColor)

        btnSelectColor.setOnClickListener {
            showColorSelectionDialog()
        }

        btnToggle.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                // Если разрешения нет, отправляем пользователя в настройки
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                toggleOverlay()
            }
        }
    }

    private fun toggleOverlay() {
        if (!overlayEnabled) {
            // Чтение настроек из полей
            val margin = etMargin.text.toString().toIntOrNull() ?: 50
            val alpha = sliderAlpha.value.toInt() // значение от 0 до 255
            val sensitivity = sliderSensitivity.value  // Чувствительность в диапазоне от 0.5 до 2.0

            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra("margin", margin)
                putExtra("alpha", alpha)
                putExtra("color", chosenColor)
                putExtra("sensitivity", sensitivity)
            }
            startService(intent)
            overlayEnabled = true
            btnToggle.text = getString(R.string.disable_overlay)
        } else {
            val intent = Intent(this, OverlayService::class.java)
            stopService(intent)
            overlayEnabled = false
            btnToggle.text = getString(R.string.enable_overlay)
        }
    }

    private fun showColorSelectionDialog() {
        val colors = arrayOf(
            getString(R.string.red),
            getString(R.string.blue),
            getString(R.string.green),
            getString(R.string.white) // добавляем белый
        )
        val colorValues = arrayOf(
            0xFFFF0000.toInt(),
            0xFF0000FF.toInt(),
            0xFF00FF00.toInt(),
            0xFFFFFFFF.toInt() // значение для белого цвета
        )
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_overlay_color)
            .setItems(colors) { _, which ->
                chosenColor = colorValues[which]
                btnSelectColor.text = colors[which]
            }
            .show()
    }
}
