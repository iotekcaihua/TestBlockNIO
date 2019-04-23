package com.caihua;

public enum Season {
    Spring("春天", "春暖花开"){
        public void show(){
            System.out.println("我是春天");
        }
    },
    Summer("夏天", "艳阳高照"){
        public void show(){
            System.out.println("我是夏天");
        }
    },
    Autumn("秋天", "秋高气爽"){
        public void show(){
            System.out.println("我是秋天");
        }
    },
    Winter("冬天", "雪花漫天"){
    };

    private final String name;
    private final String description;

    private Season(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void show(){
        System.out.println("show");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
