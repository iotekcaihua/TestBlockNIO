package com.caihua;

public class Test {
    public static void main(String[] args) {
        System.out.println(Season.Spring.getName());
        System.out.println(Season.Summer.getName());
        System.out.println(Season.Autumn.getName());
        System.out.println(Season.Winter.getName());
        System.out.println(Season.Spring.getName());
    }

    public void show(String... c){

    }
}

class SingleTon {
    private static SingleTon singleTon;
    private SingleTon(){
    }

    public static SingleTon getSingleTon(){
        if(singleTon==null){
            singleTon=new SingleTon();
        }
        return singleTon;
    }
}
