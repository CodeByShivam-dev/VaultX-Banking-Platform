package vaultx_backend.dto;

public class RegisterRequest {

    private String name;
    private String fatherName;
    private String motherName;
    private String email;
    private String panCard;
    private String phone;
    private String message;
    private Long accountNumber;


    public RegisterRequest()
    {
    }

    public String getMessage() {
        return message;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getEmail() {
        return email;
    }

    public String getPanCard() {
        return panCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}