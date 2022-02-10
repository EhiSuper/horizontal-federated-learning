function validate(){
    console.log("sono nella validate");
    if (document.getElementById("password").textContent === document.getElementById("confirmPassword").textContent) {
        window.alert("The confirm password is not equal to the previous password")
        return false;
    }
    else return true;
}