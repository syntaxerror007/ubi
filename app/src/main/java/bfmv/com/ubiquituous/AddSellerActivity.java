package bfmv.com.ubiquituous;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_add_seller)
public class AddSellerActivity extends AppCompatActivity {
    @ViewById
    EditText mETnoPenjual;

    @ViewById
    EditText mETalamat;

    @AfterViews
    void init() {
        SharedPreferences prefs = getSharedPreferences("bfmv.com.ubiquitous.sharedprefs", Context.MODE_PRIVATE);

        mETalamat.setText(prefs.getString("address", ""));
        mETnoPenjual.setText(prefs.getString("sellerNumber", ""));
    }

    @Click(R.id.fab)
    public void fabClick() {
        Log.d("MO", mETnoPenjual.getText().toString());
        Log.d("MO", mETalamat.getText().toString());
        if (mETnoPenjual.getText().toString() == null || mETnoPenjual.getText().toString().equals("")) {
            mETnoPenjual.setError("Harap isi nomer penjual");
            return;
        }
        if (mETalamat.getText().toString() == null || mETalamat.getText().toString().equals("")) {
            mETalamat.setError("Harap isi alamat Anda");
            return;
        }
        SharedPreferences prefs = getSharedPreferences("bfmv.com.ubiquitous.sharedprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("address", mETalamat.getText().toString());
        editor.putString("sellerNumber", mETnoPenjual.getText().toString());
        editor.commit();
        Toast.makeText(this, "Alamat dan nomer penjual berhasil disimpan", Toast.LENGTH_SHORT).show();
        finish();
    }
}
