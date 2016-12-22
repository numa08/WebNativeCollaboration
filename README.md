# javascript から Android のコードを非同期に実行する

WebView 内部の javascript から Android の非同期処理を呼び出してコールバックで結果を受け取るサンプル。

javascript から一般的な js のライブラリに近いスタイルで java のコードを呼び出し、コールバックでレスポンスを受け取りたい。

Android <-> javascript はプリミティブな型あるいは文字列のやりとりしかできないようなので（小数点の取扱は要調査）java に与える引数はすべて文字列とする。同様に java から javascript を呼び出すときもプリミティブな型以外は json 文字列を与えるものとする。

## javascript の実装

javascript から java を呼び出すときは `JavascriptInterface` として公開されているメソッドを呼び出すことができる。非同期処理をリクエストするときにはコールバック関数を文字列としたものを与える。


```javascript
window.userRepository.findUserById(1,
		(function(err, data) { // 第2引数にコールバック関数を与えるが、 toString で文字列化する
		   if (err) {
		     console.error("loadUser failed", err);
		   }
		   var user = JSON.parse(data); // コールバックに与えられたデータは必要に応じて JSON.parse をする
		   content.innerHTML = "user id = " + user.id + " user name = " + user.name;
		 }).toString()
		);
```

## java の実装

javascript のコードは WebView の `loadUrl` や `evaluateJavascript` で実行ができる。基本的にメソッドの最後の引数をコールバック関数用の文字列として受け取るが、無名関数が渡された場合と名前の付いた関数が渡された場合で場合分けが必要。

```java
@JavascriptInterface
public void findUserById(final long id, @Nullable final String callback) {
  userDatabase
    .findUserById(id)
    .onSuccess(new Func0<User>() {
       void func(User user) {
         final String json = gson.toJson(user); // javascript に与えるデータは json 文字列にする
	 invokeCallback(callback, null, json);
       }
     }
    .onError(new Function0<Throwable>() {
       void func(Throwable error) {
         final String json = gson.toJson(error);
         invokeCallback(callback, error);
       }
     };
}


private void invokeCallback(String callback, String error, String responses...) {
  final String function;
  if (callback.startWith("function ") { // 無名関数の場合
    function = "(" + callback + ")";
  } else {
    function = callback;
  }
  final StringBuilder args = new StringBuildr("(").append(error == null ? "null" : error);
  args.append(responses.mkString { a -> ",'" + a + "'" }).append(")") // 結果の文字列を返す。一つ一つの json は '' で囲む
  webView.evaluateJavascript(function + args), null);
}
```
