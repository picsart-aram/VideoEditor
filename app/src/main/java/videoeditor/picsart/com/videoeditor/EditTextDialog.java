package videoeditor.picsart.com.videoeditor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


/**
 * Created by Tigran Isajanyan on 6/4/15.
 */
public class EditTextDialog extends Dialog {

    private EditText editText;
    private Button setButton;
    private Button cancelButton;
    private Context context;

    private OnRadioGroupChangedListener onRadioGroupChangedListener;

    public EditTextDialog(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.edit_text_dialog);

        editText = (EditText) findViewById(R.id.edt_txt_dialog);

        setButton = (Button) findViewById(R.id.set_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRadioGroupChangedListener != null) {

                    onRadioGroupChangedListener.onRadioGroupChanged(editText.getText().toString());
                }
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setOnRadioGroupChangedListener(OnRadioGroupChangedListener l) {
        onRadioGroupChangedListener = l;
    }


    public interface OnRadioGroupChangedListener {
        void onRadioGroupChanged(String text);
    }

}
