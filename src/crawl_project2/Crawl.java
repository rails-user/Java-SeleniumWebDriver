/////////////////////////////////////////////////
//前提条件
//webdriverが操作中にはChromeは触らないこと
/////////////////////////////////////////////////

package crawl_project2;

public class Crawl {

	public static void main(String[] args) throws InterruptedException {

		//ログインID・パスワード
		final String LOGIN_ID = args[0];
		final String LOGIN_PASSWORD = args[1];

		//例外処理
		try {

			CrawlSyosetu CrawlSyosetu = new CrawlSyosetu(LOGIN_ID,LOGIN_PASSWORD);
			CrawlSyosetu.execute();

		}
		catch(Exception e) {

			System.out.println("例外が発生しました。");
			System.out.println(e);

		}
		finally {

			System.out.println("処理が終了しました。");

		}
	}
}
