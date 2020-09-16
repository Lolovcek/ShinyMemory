package renderers;

import components.SpriteRenderer;
import entities.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;


    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
        if (sprite != null) {
            add(sprite);
        } assert false : "Error: Couldn't add gameobject to batch renderer";
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : this.batches) {
            if (batch.getHasRoom()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            getBatches().add(newBatch);
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    private List<RenderBatch> getBatches() {
        return this.batches;
    }

    public void render() {
        for (RenderBatch batch: this.batches) {
            batch.render();
        }
    }

}
