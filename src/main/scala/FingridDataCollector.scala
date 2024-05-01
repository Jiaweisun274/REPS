import java.io.PrintWriter
import scalaj.http.Http
import scala.io.StdIn.readLine

object FingridDataCollector {
  def fetchAndSaveData(apiUrl: String, apiKey: String, datasetId: Int, outputFilename: String, format: String = "csv", startTime: String, endTime: String, oneRowPerTimePeriod: Boolean = false): Unit = {
    // Build the full API URL with query parameters
    val queryParams = Seq(
      "format" -> format,
      "startTime" -> startTime,
      "endTime" -> endTime,
      "oneRowPerTimePeriod" -> oneRowPerTimePeriod.toString
    ).filter(_._2.nonEmpty).map { case (k, v) => s"$k=$v" }.mkString("&")

    val fullUrl = s"$apiUrl/$datasetId/data?$queryParams"

    // Make HTTP request to Fingrid API, with the API key in the headers
    val response = Http(fullUrl)
      .header("x-api-key", apiKey)
      .asString

    // Check if the request was successful
    if (response.is2xx) {
      // Write the response body to a CSV file
      val writer = new PrintWriter(outputFilename)
      writer.write(response.body)
      writer.close()
      println(s"Data successfully saved to $outputFilename")
    } else {
      println(s"Error fetching data: ${response.statusLine}")
      println(s"Response Body: ${response.body}")
    }
  }

  def main(args: Array[String]): Unit = {
    // Ask the user to choose the data type
    println("Choose the type of data to fetch:")
    println("1. Solar panels")
    println("2. Wind turbines")
    println("3. Hydropower")
    val choice = readLine("Enter the number of your choice: ").trim.toInt

    // Map user choice to dataset ID
    val (datasetId, dataType) = choice match {
      case 1 => (247, "solar_panels")
      case 2 => (181, "wind_turbines")
      case 3 => (191, "hydropower")
      case _ =>
        println("Invalid choice. Exiting.")
        return
    }

    // Ask for start and end time
    val startTime = readLine("Enter start time (format: 2023-01-01T00:00:00Z): ").trim
    val endTime = readLine("Enter end time (format: 2023-01-01T23:59:59Z): ").trim

    // Set API URL and API key
    val apiUrl = "https://data.fingrid.fi/api/datasets"
    val apiKey = "ab159c9a089a4a43882b487f0c2d0390"
    val outputFilename = s"${dataType}_forecast.csv" // Adjust extension as needed

    // Fetch and save data
    fetchAndSaveData(apiUrl, apiKey, datasetId, outputFilename, format = "csv", startTime = startTime, endTime = endTime)
  }
}
