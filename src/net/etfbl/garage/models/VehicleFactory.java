package net.etfbl.garage.models;

import net.etfbl.garage.models.departments.firefighters.FirefighterVan;
import net.etfbl.garage.models.departments.medical.MedicalVan;
import net.etfbl.garage.models.departments.police.PoliceCar;

import java.util.Random;

public class VehicleFactory {
    private static String[] carBrands = {"Nissan", "Audi", "Ford", "Volkswagen", "Ferrari", "Opel", "Mercedes", "Peugot", "Tesla",
            "Lamborghini", "Citroen", "Mazda", "Skoda", "Cadillac", "Range Rover", "Lexus", "Mitsubishi", "Hyundai", "Honda", "BMW" };
    private static String[] motorBrands = { "Yamaha", "Honda", "Ducati", "Kawasaki", "Harley-Davidson", "Suzuki"};

    private String randomNumbersAndLetters(int num) {
        Random random = new Random();
        StringBuilder strRes = new StringBuilder();
        for(int i=0; i<num ; i++) {
            char letter = (char)random.nextInt(36);
            if(letter>25) {
                letter-=26;
                letter+='0';
            } else {
                letter += 'A';
            }
            strRes.append(letter);
        }
        return strRes.toString();
    }

    private String randomCarName() {
        Random random = new Random();
        return carBrands[random.nextInt(carBrands.length)] + " " + randomNumbersAndLetters(4);
    }

    public Vehicle constructRandomVehicle() {
        Random randomNum = new Random();
        int type = 1+randomNum.nextInt(3);
        boolean isSpecial = Math.random() > 0.9;
        if (isSpecial) {
            if (type == 1) { //van
                return new FirefighterVan(randomCarName(), randomNumbersAndLetters(6),randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            } else if (type == 2) {
                return new PoliceCar(randomCarName() , randomNumbersAndLetters(6) ,randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            } else {
                return new MedicalVan(randomCarName() , randomNumbersAndLetters(6) ,randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            }
        } else {
            if (type == 1) { //van
                return new Motorbike(randomCarName() , randomNumbersAndLetters(6) ,randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            } else if (type == 2) {
                return new Van(randomCarName() , randomNumbersAndLetters(6) ,randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            } else {
                return new Car(randomCarName() , randomNumbersAndLetters(6) ,randomNumbersAndLetters(5) , randomNumbersAndLetters(3)+"-"+randomLetter()+"-"+randomNumbersAndLetters(3) );
            }
        }
    }

    private String randomLetter() {
        Random random = new Random();
        return Character.toString((char)(random.nextInt(26)+'A'));
    }
}
