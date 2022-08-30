package arigato.onichaan.meherbaka.model;

import arigato.onichaan.meherbaka.annotations.AutoIncrement;
import arigato.onichaan.meherbaka.annotations.NameToName;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONObject;

public class Employee {
    @AutoIncrement
    private int id;
    @NameToName(value = "first_name")
    @JsonProperty(value = "first_name")
    private String firstName;
    @NameToName(value = "last_name")
    @JsonProperty(value = "last_name")
    private String lastName;
    private String email;
    private String gender;
    private int age;
    private int salary;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                '}';
    }
    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName",firstName);
        jsonObject.put("lastName",lastName);
        jsonObject.put("email",email);
        jsonObject.put("gender",gender);
        jsonObject.put("salary",salary);
        return jsonObject.toString();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }
}
