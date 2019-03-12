package com.example.artutorial1;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ArFragment fragment;
    private ModelRenderable modelRenderable;
    private static final String DUCK_ASSET =
            "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF-Binary/Duck.glb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        renderURI(DUCK_ASSET, RenderableSource.SourceType.GLB, 0.1f);
        //renderAndy();
        setupTapListener(fragment);
    }

    void setupTapListener(ArFragment fragment) {
        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }
                    // Create the anchor
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(fragment.getArSceneView().getScene());
                    // Create the transformable andy and add it to the anchor
                    TransformableNode andy = new TransformableNode(fragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(modelRenderable);
                    andy.select();
                }
        );
    }

    void renderURI(String asset, RenderableSource.SourceType type, float scale) {
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(asset),
                        type)
                        .setScale(scale)  // Scale the original model to 50%.
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(asset)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e("FragmentActivity", "Unable to load Renderable.", throwable);
                            return null;
                        });
    }

    void renderRandomURI() {
        List<RenderableSource.SourceType> types = Arrays.asList(RenderableSource.SourceType.GLB,
                                                                RenderableSource.SourceType.GLB,
                                                                RenderableSource.SourceType.GLB);
        List<Float> scales = Arrays.asList(0.1f, 0.05f, 1.f);
        List<String> URIs = Arrays.asList(
                "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF-Binary/Duck.glb",
                "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/CesiumMilkTruck/glTF-Binary/CesiumMilkTruck.glb",
                "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Avocado/glTF-Binary/Avocado.glb"
        );
        Random rand = new Random();
        //int randomElement = givenList.get(rand.nextInt(givenList.size()));
        int ind = rand.nextInt(3);
        renderURI(URIs.get(ind), types.get(ind), scales.get(ind));
    }

    void renderAndy() {
        ModelRenderable.builder()
        .setSource(this, R.raw.andy)
        .build()
        .thenAccept(renderable -> modelRenderable = renderable)
        .exceptionally(
                throwable -> {
                    Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
    }
}
