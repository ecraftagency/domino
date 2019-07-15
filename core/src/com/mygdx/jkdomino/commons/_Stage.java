package com.mygdx.jkdomino.commons;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class _Stage extends Stage {
    private float timeToCameraZoomTarget, cameraZoomTarget, cameraZoomOrigin, cameraZoomDuration;
    private float timeToCameraMoveTarget, cameraMoveDuration;
    private Vector2 cameraMoveTarget, cameraMoveOrigin;

    private Runnable moveComplete, zoomComplete;

    private Interpolation zoomStyle;
    private Interpolation moveStyle;
    private OrthographicCamera camera;

    public _Stage() {
        super();
        timeToCameraZoomTarget = -1;
        cameraZoomTarget = 0;
        cameraZoomOrigin = 0;
        cameraZoomDuration = 0;
        zoomStyle = Interpolation.linear;

        timeToCameraMoveTarget = -1;
        moveStyle = Interpolation.linear;

        moveComplete = null;
        zoomComplete = null;
    }

    @Override
    public void setViewport(Viewport viewport) {
        super.setViewport(viewport);
        camera = (OrthographicCamera) getCamera();
    }

    public void move(Vector2 delta, float duration, Interpolation moveStyle, Runnable onComplete) {
        this.moveStyle = moveStyle;
        moveComplete = onComplete;
        cameraMoveTarget = delta;
        timeToCameraMoveTarget = cameraMoveDuration = duration;
        cameraMoveOrigin = new Vector2(camera.position.x, camera.position.y);
    }

    public void zoom(float zoomTo, float duration, Interpolation zoomStyle, Runnable onComplete) {
        this.zoomStyle = zoomStyle;
        zoomComplete = onComplete;
        cameraZoomTarget = zoomTo;
        timeToCameraZoomTarget = cameraZoomDuration = duration;
        cameraZoomOrigin = camera.zoom;
    }

    @Override
    public void act(float delta) {
        if (timeToCameraMoveTarget >= 0) {
            timeToCameraMoveTarget -= delta;
            float mProgress = timeToCameraMoveTarget < 0 ? 1 : 1f - timeToCameraMoveTarget / cameraMoveDuration;
            camera.position.x = moveStyle.apply(cameraMoveOrigin.x, cameraMoveTarget.x, mProgress);
            camera.position.y = moveStyle.apply(cameraMoveOrigin.y, cameraMoveTarget.y, mProgress);
            if (mProgress == 1 && moveComplete != null) {
                moveComplete.run();
                moveComplete = null;
            }
        }

        if (timeToCameraZoomTarget >= 0) {
            timeToCameraZoomTarget -= delta;
            float progress = timeToCameraZoomTarget < 0 ? 1 : 1f - timeToCameraZoomTarget / cameraZoomDuration;
            camera.zoom = zoomStyle.apply(cameraZoomOrigin, cameraZoomTarget, progress);
            if (progress == 1 && zoomComplete != null) {
                zoomComplete.run();
                zoomComplete = null;
            }
        }
        super.act(delta);
    }
}