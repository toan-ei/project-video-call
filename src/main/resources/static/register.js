const ipConfigRegister = {
    BASE_URL: '192.168.88.175'
};

function formRegister(){
    let username = document.getElementById('reg-username').value;
    let password = document.getElementById('reg-password').value;
    let passwordConfirm = document.getElementById('reg-confirm-password').value;
    let fullname = document.getElementById('reg-full-name').value;

    if(!username || !password || !fullname){
        alert('thông tin không hợp lệ');
        return;
    }

    if(password !== passwordConfirm){
        alert('password va password confirm khong giong nhau');
        return;
    }

    let data = {
        "username": username,
        "password": password,
        "fullname": fullname
    }
    createUser(data);
}

function createUser(data){
    const urlCreateUser = "https://" + ipConfigRegister.BASE_URL + ":8080/users/createUser";
    fetch(urlCreateUser, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    })
    .then((response) =>{
        if(!response.ok){
          throw new Error('dang ki that bai');
        }
        console.log('dang ki thanh cong');
        return response.json();
      })
    .then((response) => {
        alert('dang ki thanh cong');
        console.log(response);
    })
    .catch((error) => console.log('loi tao tai khoan ' + error))

}

function start(){
    let registerBtn = document.getElementById('register-submit');
    registerBtn.onclick = () => {
        formRegister();
    }
}
document.addEventListener('DOMContentLoaded', function(){
    start();
});
