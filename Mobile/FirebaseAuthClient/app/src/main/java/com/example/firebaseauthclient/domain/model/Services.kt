import com.google.firebase.firestore.IgnoreExtraProperties

import java.util.Date

data class Services(
    var id: String = "",
    val client: String = "",
    val user_id: String = "",
    val i_date: String = "",  // Store as String
    val f_date: String = "",  // Store as String
    val price: String = "0.0",
    val description: String = "",
    val status: String = ""
) {
    fun getPriceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0

    // You can add a helper function to parse the date when needed
    fun getParsedIDate(): Date? = i_date.toDateOrNull()
    fun getParsedFDate(): Date? = f_date.toDateOrNull()

    private fun String.toDateOrNull(): Date? {
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()) // Adjust the format to match your date string format
            format.parse(this)
        } catch (e: Exception) {
            null
        }
    }
}
