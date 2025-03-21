package stop.dizziness.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.sqrt

class DotOverlayView(context: Context) : View(context) {

    private var margin: Int = 50
    private var dotColor: Int = Color.RED
    private var dotAlpha: Int = 128

    private var sensorX: Float = 0f
    private var sensorY: Float = 0f

    // Настройка кисти для рисования точек
    private val paint = Paint().apply {
        color = dotColor
        alpha = dotAlpha
        isAntiAlias = true
    }

    private val baseRadius = 20f

    // Метод для установки настроек оверлея
    fun setConfig(margin: Int, color: Int, alpha: Int) {
        this.margin = margin
        this.dotColor = color
        this.dotAlpha = alpha
        paint.color = dotColor
        paint.alpha = dotAlpha
        invalidate()
    }

    // Обновление данных с акселерометра
    fun updateSensorData(x: Float, y: Float) {
        sensorX = x
        sensorY = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = canvas.width
        val height = canvas.height

        // Коэффициент для смещения точек, подбирается экспериментально
        val factor = 3f
        val offsetX = sensorX * factor
        val offsetY = sensorY * factor

        // Масштабирование размера точек в зависимости от величины ускорения
        val acceleration = sqrt(sensorX * sensorX + sensorY * sensorY)
        val scaleFactor = 1 + (acceleration / 10f)
        val radius = baseRadius * scaleFactor

        // Вычисляем вертикальные позиции для трёх точек (равномерно)
        val positionsY = listOf(height / 4f, height / 2f, 3 * height / 4f)

        // Рисуем точки слева
        for (posY in positionsY) {
            val x = margin.toFloat() + offsetX
            val y = posY + offsetY
            canvas.drawCircle(x, y, radius, paint)
        }

        // Рисуем точки справа
        for (posY in positionsY) {
            val x = width - margin.toFloat() + offsetX
            val y = posY + offsetY
            canvas.drawCircle(x, y, radius, paint)
        }
    }
}
