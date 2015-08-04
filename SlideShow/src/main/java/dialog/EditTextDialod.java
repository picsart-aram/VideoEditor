package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;


import com.example.intern.picsartvideo.R;


/**
 * Created by Tigran Isajanyan on 6/4/15.
 */
public class EditTextDialod extends Dialog {

    private EditText editText;
    private RadioGroup radioGroupSize;
    private RadioGroup radioGroupColor;
    private Button setButton;
    private Button cancelButton;
    private Context context;

    private OnRadioGroupChangedListener onRadioGroupChangedListener;

    public EditTextDialod(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.edit_text_dialog);

        editText = (EditText) findViewById(R.id.edt_txt_dialog);

        radioGroupSize = (RadioGroup) findViewById(R.id.radio_group_size);
        radioGroupColor = (RadioGroup) findViewById(R.id.radio_group_color);
        setButton = (Button) findViewById(R.id.set_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRadioGroupChangedListener != null) {

                    int radioButtonID = radioGroupSize.getCheckedRadioButtonId();
                    View radioButton = radioGroupSize.findViewById(radioButtonID);
                    int idx = radioGroupSize.indexOfChild(radioButton);
                    onRadioGroupChangedListener.onRadioGroupChanged(idx, radioGroupColor.indexOfChild(radioGroupColor.findViewById(radioGroupColor.getCheckedRadioButtonId())), editText.getText().toString());
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
        void onRadioGroupChanged(int shapeIndex, int colorIndex, String text);
    }

}
