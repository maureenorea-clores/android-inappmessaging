package com.rakuten.tech.mobile.inappmessaging.runtime.view

import android.content.Context
import android.util.AttributeSet
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.R
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Message
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger

internal class InAppMessageHtmlView(
    context: Context,
    attrs: AttributeSet?
): InAppMessageBaseView(context, attrs)
{

    override fun populateViewData(message: Message) {
        super.populateViewData(message)

//        setCloseButton()

        val webView: WebView = findViewById(R.id.html_web_view)

        // Basic sample
//        val unencodedHtml = """
//            <!DOCTYPE html>
//            <html>
//            <meta name="viewport" content="width=device-width, initial-scale=1">
//            <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
//            <body>
//
//            <div class="w3-container w3-green">
//              <h1>W3Schools Demo</h1>
//              <p>Resize this responsive page!</p>
//            </div>
//
//            <div class="w3-row-padding">
//              <div class="w3-third">
//                <h2>London</h2>
//                <p>London is the capital city of England.</p>
//                <p>It is the most populous city in the United Kingdom,
//                with a metropolitan area of over 13 million inhabitants.</p>
//              </div>
//
//              <div class="w3-third">
//                <h2>Paris</h2>
//                <p>Paris is the capital of France.</p>
//                <p>The Paris area is one of the largest population centers in Europe,
//                with more than 12 million inhabitants.</p>
//              </div>
//
//              <div class="w3-third">
//                <h2>Tokyo</h2>
//                <p>Tokyo is the capital of Japan.</p>
//                <p>It is the center of the Greater Tokyo Area,
//                and the most populous metropolitan area in the world.</p>
//              </div>
//            </div>
//            </body>
//            </html>
//        """.trimIndent()

        // With remote image and JavaScript sample
        val unencodedHtml = """
            <!DOCTYPE html>
            <html>
               <head>
                  <style>
                     body {
                     background-color: aquamarine;
                     text-align: center;
                     }
                     .responsive {
                       width: 100%;
                       height: auto;
                     }
                    .button {
                      background-color: #555555;
                      border: none;
                      color: white;
                      padding: 15px 32px;
                      text-align: center;
                      text-decoration: none;
                      display: inline-block;
                      font-size: 16px;
                      margin: 4px 2px;
                      cursor: pointer;
                      border-radius: 8px;
                    }
                  </style>
                  
                  <script>
                    const closeCampaign = () => {
                        IamJsInterface.closeCampaign();
                    };
                  </script>
               </head>
               
               <body>
                  <div>
                     <h2>Up to 5000 points!</h2>
                     <img src="https://pay.rakuten.co.jp/campaign/invitation/img/invite_web/img_01_20230508.png" alt="campaign1" class="responsive">
                  </div>
                    
				  <div>
                     <h2>Up to 10 million points!</h2>
                     <img src="https://kuniriki-lau.com/wp-content/uploads/2021/01/rakuten_pay_bank_payment_ok_campaign.png" alt="campaign2" class="responsive">
                  </div>  
                  
                  <div>
                     <button class="button" onclick="closeCampaign()">Close</button>
                  </div>
               </body>
            </html>
        """.trimIndent()

        // Carousel sample using CSS
//        val unencodedHtml = """
//            <!DOCTYPE html>
//            <html>
//               <meta name="viewport" content="width=device-width, initial-scale=1">
//               <head>
//                  <style>
//                      body {
//                        height: 600px;
//                        margin: 0;
//                        display: grid;
//                        grid-template-rows: 500px 100px;
//                        grid-template-columns: 1fr 30px 30px 30px 30px 30px 1fr;
//                        align-items: center;
//                        justify-items: center;
//                      }
//
//                      main#carousel {
//                        grid-row: 1 / 2;
//                        grid-column: 1 / 8;
//                        width: 100vw;
//                        height: 500px;
//                        display: flex;
//                        align-items: center;
//                        justify-content: center;
//                        overflow: hidden;
//                        transform-style: preserve-3d;
//                        perspective: 600px;
//                        --items: 5;
//                        --middle: 3;
//                        --position: 1;
//                        pointer-events: none;
//                      }
//
//                      div.item {
//                        position: absolute;
//                        width: 300px;
//                        height: 400px;
//                        background-color: coral;
//                        --r: calc(var(--position) - var(--offset));
//                        --abs: max(calc(var(--r) * -1), var(--r));
//                        transition: all 0.25s linear;
//                        transform: rotateY(calc(-10deg * var(--r)))
//                          translateX(calc(-300px * var(--r)));
//                        z-index: calc((var(--position) - var(--abs)));
//                      }
//
//                      div.item:nth-of-type(1) {
//                        --offset: 1;
//                        background-color: #90f1ef;
//                      }
//                      div.item:nth-of-type(2) {
//                        --offset: 2;
//                        background-color: #ff70a6;
//                      }
//                      div.item:nth-of-type(3) {
//                        --offset: 3;
//                        background-color: #ff9770;
//                      }
//                      div.item:nth-of-type(4) {
//                        --offset: 4;
//                        background-color: #ffd670;
//                      }
//                      div.item:nth-of-type(5) {
//                        --offset: 5;
//                        background-color: #e9ff70;
//                      }
//
//                      input:nth-of-type(1) {
//                        grid-column: 2 / 3;
//                        grid-row: 2 / 3;
//                      }
//                      input:nth-of-type(1):checked ~ main#carousel {
//                        --position: 1;
//                      }
//
//                      input:nth-of-type(2) {
//                        grid-column: 3 / 4;
//                        grid-row: 2 / 3;
//                      }
//                      input:nth-of-type(2):checked ~ main#carousel {
//                        --position: 2;
//                      }
//
//                      input:nth-of-type(3) {
//                        grid-column: 4 /5;
//                        grid-row: 2 / 3;
//                      }
//                      input:nth-of-type(3):checked ~ main#carousel {
//                        --position: 3;
//                      }
//
//                      input:nth-of-type(4) {
//                        grid-column: 5 / 6;
//                        grid-row: 2 / 3;
//                      }
//                      input:nth-of-type(4):checked ~ main#carousel {
//                        --position: 4;
//                      }
//
//                      input:nth-of-type(5) {
//                        grid-column: 6 / 7;
//                        grid-row: 2 / 3;
//                      }
//                      input:nth-of-type(5):checked ~ main#carousel {
//                        --position: 5;
//                      }
//                  </style>
//               </head>
//               <body>
//                  <input type="radio" name="position" checked />
//                  <input type="radio" name="position" />
//                  <input type="radio" name="position" />
//                  <input type="radio" name="position" />
//                  <input type="radio" name="position" />
//                  <main id="carousel">
//                    <div class="item"></div>
//                    <div class="item"></div>
//                    <div class="item"></div>
//                    <div class="item"></div>
//                    <div class="item"></div>
//                    <main>
//                </body>
//            </html>
//        """.trimIndent()

        val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")
        webView.addJavascriptInterface(IamJsInterface(message), "IamJsInterface")
        webView.settings.javaScriptEnabled = true
    }
}

internal class IamJsInterface(private val message: Message) {

    /**
     * Closes the campaign
     */
    @JavascriptInterface
    fun closeCampaign() {
        InAppLogger("IamJsInterface").debug("closeCampaign() ${message.campaignId}")
        InAppMessaging.instance().closeMessage()
    }
}