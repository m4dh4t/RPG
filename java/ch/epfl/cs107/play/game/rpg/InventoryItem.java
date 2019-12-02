package ch.epfl.cs107.play.game.rpg;

public class InventoryItem {
    private String name;
    private float weight;
    private int price;

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getWeight() {
        return weight;
    }
}
