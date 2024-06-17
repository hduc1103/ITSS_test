package views.screen.payment;

import controller.PaymentController;
import entity.invoice.Invoice;
import entity.response.Response;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import utils.Configs;
import views.screen.BaseScreenHandler;
import views.screen.invoice.InvoiceScreenHandler;

import java.io.IOException;
import java.util.Objects;

public class PaymentScreenHandler extends BaseScreenHandler {
	public Response response;
	@FXML
	private Button btnConfirmPayment;

	@FXML
	private ImageView loadingImage;

	@FXML
	private Label paymentLink;

	@FXML
	private Button btnGoToLink;

	@FXML
	private VBox vBox;

	private Invoice invoice;

	@FXML
	private ImageView back;

	private ResultScreenHandler resultScreenHandler;
	public PaymentScreenHandler(Stage stage, String screenPath, int amount, String contents) throws IOException {
		super(stage, screenPath);
	}

	private void displayWebView() {
		var paymentController = new PaymentController();
		var paymentUrl = paymentController.getUrlPay(invoice.getAmount(), "Thanh toan hoa don AIMS");
		System.out.println(paymentUrl);
		WebView paymentView = new WebView();
		WebEngine webEngine = paymentView.getEngine();
		System.out.println(paymentUrl);
		webEngine.load(paymentUrl);
		webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
			// Xử lý khi URL thay đổi
			System.out.println(newValue);
			//this.setResponse(handleUrlChanged(newValue));
			if(newValue.contains("http://127.0.0.1:50387/?")) {
				response = new Response(newValue);
				//System.out.println(invoice.getAmount());
				//System.out.println("Amount VNPay: "+ response.getVnp_Amount());
				if(Objects.equals(response.getVnp_ResponseCode(), "00")){
					try {
						paymentController.emptyCart();
						System.out.println("Successful Payment");
						BaseScreenHandler ResultScreenHandler = new ResultScreenHandler(this.stage, Configs.RESULT_SCREEN_PATH, "PAYMENT RESULT", "SUCESSFULL!", String.valueOf(invoice.getId()), response.getVnp_BankCode(), response.getVnp_BankTranNo(), String.valueOf(invoice.getAmount()), response.getVnp_TransactionStatus(), response.getVnp_PayDate());
						ResultScreenHandler.setPreviousScreen(this);
						ResultScreenHandler.setScreenTitle("Result");
						ResultScreenHandler.show();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						BaseScreenHandler InvoiceScreenHandler = new InvoiceScreenHandler(this.stage, Configs.INVOICE_SCREEN_PATH, invoice);
						InvoiceScreenHandler.setScreenTitle("Invoice Screen");
						InvoiceScreenHandler.show();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				}
			}
		});
		vBox.getChildren().clear();
		vBox.getChildren().add(paymentView);
	}

	public PaymentScreenHandler(Stage stage, String screenPath, Invoice invoice) throws IOException {
		super(stage, screenPath);
		this.invoice = invoice;
		back.setOnMouseClicked(e -> {
			setScreenTitle("Invoice screen");
			this.getPreviousScreen().show();
		});
		displayWebView();
	}
}