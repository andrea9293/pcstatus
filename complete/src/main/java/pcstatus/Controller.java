package pcstatus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Controller {

    @FXML private Label miscellaneous;
    @FXML private ImageView cpuImage;
    @FXML private Label ipText;
    @FXML private Label cpuText;
    @FXML private Label batteryText;
    @FXML private Label disksText;

    @FXML
    private void initialize() {
        //textField.setText("I'm a Label.");

    }

    public void setMiscellaneous (String miscellaneous){
        this.miscellaneous.setText(miscellaneous);
    }

    public void setCpuImage(String cpu) {
        cpuImage.setImage(new Image(cpu));
    }

    public void setIpText(String ipText) {
        this.ipText.setText(ipText);
    }

    @FXML
    public void setBatteryText(String labelTexts) {
        batteryText.setText(labelTexts);
    }

    @FXML
    public void setCpuText(String labelTexts) {
        cpuText.setText(labelTexts);
    }

    @FXML
    public void setDisksText(String disksText) {
        this.disksText.setText(disksText);
    }
}
