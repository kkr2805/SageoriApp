package com.hgtech.sageoriapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;

public class PreViewImageDialog extends Dialog {

    Bitmap imageBitmap;

    PreViewImageDialog(Context context, Bitmap image) {
        super(context);

        this.imageBitmap = image;
        setContentView(R.layout.preview_image_dialog);
        setCancelable(true);

        ImageView previewImageView = (ImageView)findViewById(R.id.imageView);
        previewImageView.setImageBitmap(imageBitmap);
    }

}
