package org.oldfather.xianhs.web

import io.github.bonigarcia.wdm.WebDriverManager
import mu.KotlinLogging
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait

import org.springframework.web.bind.annotation.*
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import java.io.*
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse


/**
 * Created by ChenChang on 2022/11/1.
 */
@RestController
@RequestMapping("/api/screenShot")
class ScreenShotController {
    private val logger = KotlinLogging.logger {}


    @RequestMapping("/getXianHSJG", method = [RequestMethod.GET])
    @ResponseBody
    @Throws(Exception::class)
    fun getXianHSJG(response: HttpServletResponse, name: String, cardNo: String): String {
        WebDriverManager.chromedriver().setup()
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--no-sandbox")
        chromeOptions.addArguments("--headless")
        chromeOptions.addArguments("disable-gpu")
        val driver = ChromeDriver(chromeOptions)
        driver["https://yqpt.xa.gov.cn/nrt/resultQuery.html"]
        val js = "localStorage.setItem('inquire-name', '${name}');" +
                "localStorage.setItem('inquire-card', '${cardNo}');"
        WebDriverWait(driver, 300).until { d: WebDriver ->
            (d as JavascriptExecutor)
                .executeScript(js)
            (d as JavascriptExecutor)
                .executeScript("return document.readyState") == "complete"
        }
        driver.navigate().refresh()
        driver.executeScript(" document.documentElement.style.overflowY = 'hidden'")
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        driver.manage().window().size = Dimension(375, 667)

        val screenshot = AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver)
        val image = screenshot.image
        driver.manage().window().maximize()
        val tmpNoPrefix: Path = Files.createTempDirectory(null)
        val fileName = "${UUID.randomUUID()}.png"
        val pngFilePath =
            tmpNoPrefix.toAbsolutePath().toString() + File.separator + fileName
        val imageFile = File(pngFilePath)
        ImageIO.write(image, "PNG", imageFile)
        response.reset()
        response.contentType = "image/png"
        response.characterEncoding = "utf-8"
        response.setContentLength(imageFile.length().toInt())

        response.setHeader(
            "Content-Disposition",
            "attachment;filename=${URLEncoder.encode(fileName, "utf-8")}"
        )
        try {
            BufferedInputStream(FileInputStream(imageFile)).use { bis ->
                val buff = ByteArray(1024)
                val os: OutputStream = response.outputStream
                var i: Int
                while (bis.read(buff).also { i = it } != -1) {
                    os.write(buff, 0, i)
                    os.flush()
                }
            }
        } catch (e: IOException) {
            logger.error { e }
            driver.quit()
            return "下载失败"
        }
        driver.quit()
        return "下载成功"
    }

}

