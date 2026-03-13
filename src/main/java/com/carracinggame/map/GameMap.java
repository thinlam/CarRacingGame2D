package com.carracinggame.map;

import javafx.scene.canvas.GraphicsContext;

public interface GameMap {
    void update(double scrollSpeed);
    void render(GraphicsContext gc);

    double getTrackX();
    double getTrackW();
    int getLanes();
}
