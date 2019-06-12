package ifpr.br.imageuploadtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestStoragePermission();
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editTextName);
    }

    public void uploadMultipart(View v) {
        String name = editText.getText().toString().trim();
        try {
            new MultipartUploadRequest(getApplicationContext(), Constants.UPLOAD_URL)
                    .addFileToUpload(filePath, "image")
                    .addParameter("name", name)
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showFileChooser(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = RealPathUtils.getRealPathFromURI(this, data.getData());
            System.out.println("FILE PATH: " + filePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permissão negada!", Toast.LENGTH_LONG).show();
            }
        }
    }

}