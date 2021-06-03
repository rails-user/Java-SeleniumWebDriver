/////////////////////////////////////////////////
//前提条件
//webdriverが操作中にはChromeは触らないこと
//headlessモードにしたい場合は33行目のコメントを外すこと
/////////////////////////////////////////////////

package crawl_project2;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

public class CrawlSyosetu{

	//メンバ変数（ログインID・パスワード）
	private String loginId;
	private String loginPassword;

	//コンストラクタ
	CrawlSyosetu(String LOGIN_ID,String LOGIN_PASSWORD){
		loginId = LOGIN_ID;
		loginPassword = LOGIN_PASSWORD;
	}

	public void execute() throws InterruptedException{

		//Chrome WebDroverを生成
		ChromeOptions options = new ChromeOptions();
		//headlessモードを指定
		//options.addArguments("--headless");
		System.setProperty("webdriver.chrome.driver", "exe/chromedriver");
		ChromeDriverService driverService = ChromeDriverService.createDefaultService();
		WebDriver driver = new ChromeDriver(driverService, options);
		//小説家になろうに遷移
		driver.get("https://ssl.syosetu.com/login/input/");
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
		//ログイン関数の呼び出し
		login(driver,loginId,loginPassword);
		//ランキングページに遷移
		driver.get("https://yomou.syosetu.com/rank/list/type/daily_total/");
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);

		//一覧表示されたランキングの処理を順次実行する
		for (int i = 1; i <= driver.findElements(By.className("tl")).size(); i++){
			//ランキング300の一覧表示された個別リンクをクリック
			driver.findElement(By.id("best"+i)).click();
			//ダウンロード後、3秒（3000ミリ秒）待機
			Thread.sleep(3000);

			//左から一番目のタブのハンドルを保持
			String Win_Handle_1 = driver.getWindowHandle();

			//左から二番目のタブの処理に進む
			for (String Win_Handle_2 : driver.getWindowHandles()) {
				if(!Win_Handle_1.contentEquals(Win_Handle_2)) {
					//左から二番目のタブにハンドルを移動する
					driver.switchTo().window(Win_Handle_2);
					//リンク「TXTダウンロード」のURLに遷移する
					WebElement Link = driver.findElement(By.linkText("TXTダウンロード"));
					String href = Link.getAttribute("href");
					driver.get(href);
					driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
					//ダウンロード関数の呼び出し
					download(driver);
					//左から二番目のタブを閉じる
					driver.close();
					Thread.sleep(3000);
					driver.switchTo().window(Win_Handle_1);
					Thread.sleep(3000);
					break;
				}
			}
		}
		driver.quit();
		//例外処理
	}

	//ログイン関数
	private static void login(WebDriver driver,String loginId,String loginPassword){
		WebElement elmId = driver.findElement(By.name("narouid"));
		elmId.sendKeys(loginId);
		WebElement elmPass = driver.findElement(By.name("pass"));
		elmPass.sendKeys(loginPassword);
		WebElement elmSubmit = driver.findElement(By.id("mainsubmit"));
		elmSubmit.click();
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
	}

	//ダウンロード関数
	private static void download(WebDriver driver) throws InterruptedException {
		//ダウンロード実行（セレクトボックスが存在すれば、セレクトボックスを順次選択し、「ダウンロードを実行します」ボタンをクリック）
		if(driver.findElements(By.name("no")).size()>0) {
			//ボタンとセレクトボックスの要素を取得
			WebElement btnSubmit = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/input"));
			WebElement selectBox = driver.findElement(By.name("no"));
			Select selectText = new Select(selectBox);
			//セレクトボックスを順次選択し、実行
			for(int j=0 ; j<selectText.getOptions().size(); j++) {
				selectText.selectByIndex(j);
				btnSubmit.click();
				while(true) {
					if(btnSubmit.isEnabled()) {
						break;
					}
				}
			}
			//ダウンロード実行（セレクトボックスが存在しなければ、ダウンロードを実行します」ボタンをクリック）
		}else{
			WebElement btnSubmit = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td/input"));
			btnSubmit.click();
			Thread.sleep(10000);
		}
	}
}
