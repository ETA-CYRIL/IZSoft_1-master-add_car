public enum TrafficLightColor {
    GREEN("Green"), YELLOW("Yellow"), RED("Red");

    private final String name;

    TrafficLightColor(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
