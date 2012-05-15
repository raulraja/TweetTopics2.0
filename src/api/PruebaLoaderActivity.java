package api;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.response.ErrorResponse;
import api.response.Export2HTMLResponse;
import com.javielinux.tweettopics2.BaseActivity;

public class PruebaLoaderActivity extends BaseActivity {

    private TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);

        text = new TextView(this);
        text.setTextColor(Color.BLACK);

        ll.addView(text);

        Button bt = new Button(this);
        bt.setText("Pruebame");

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click();
            }
        });

        ll.addView(bt);

        setContentView(ll);

    }

    private void click() {
        APITweetTopics.export2HTML(this, this.getLoaderManager(), new APIDelegate<Export2HTMLResponse>() {
            @Override
            public void onResults(Export2HTMLResponse result) {
                text.setText("OnResult");
            }

            @Override
            public void onError(ErrorResponse error) {
                text.setText("OnError");
            }
        });
    }

}
