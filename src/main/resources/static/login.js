const ipConfigLogin = {
    BASE_URL: '192.168.88.175'
};

function formLogin(){
  let username = document.getElementById('username').value;
  let password = document.getElementById('password').value;

  if(!username || !password){
    alert('vui long nhap day du thong tin');
    return;
  }

  let data = {
    "username": username,
    "password": password
  }

  login(data);

}

function login(data){
  const urlLogin = 'https://' + ipConfigLogin.BASE_URL + ':8080/authentication/login';
  fetch(urlLogin, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data)
  })
  .then((response) =>{
    if(!response.ok){
      throw new Error('dang nhap that bai');
    }
    console.log('dang nhap thanh cong');
    return response.json();
  })
  .then((response) => {
    const overlay = document.getElementById('authOverlay');
    const loginForm = document.getElementById('login');
    const registerForm = document.getElementById('register');

    overlay.classList.remove('active');
    loginForm.classList.remove('active');
    registerForm.classList.remove('active');
    localStorage.setItem("token", response.token);
    const now = Date.now();
    const expired = now + 3600 * 1000;
    localStorage.setItem("expiredTime", expired);
    console.log(response);
    const urlGetMyInfo = 'https://' + ipConfigLogin.BASE_URL + ':8080/users/myinfo';
    fetch(urlGetMyInfo, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "application/json"
        }
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        localStorage.setItem("username", data.username);
        localStorage.setItem("fullname", data.fullname);
        location.reload();
    })
    .catch(error => console.error("loi get my info: ", error));
  })
  .catch((error) => {
    console.log('loi dang nhap ' + error)
  })
}

function logout(){
  const urlLogout = 'https://' + ipConfigLogin.BASE_URL + ':8080/authentication/logout';
  let token = localStorage.getItem("token");
  let dataToken = {
    "token": token
  }
  fetch(urlLogout, {
    method: "POST",
    headers: {
      "Authorization": "Bearer " + token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(dataToken)
  })
  .then((response) => {
    if(!response.ok){
      throw new Error("dang xuat that bai");
    }
    console.log('dang xuat thanh cong');
    alert('ban da dang xuat thanh cong');
  })
  .catch((err) => {
    console.log('loi dang xuat: ' + err);
  })
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  location.reload();
}

function start(){
  document.addEventListener('DOMContentLoaded', function () {
    let loginBtn = document.getElementById('submit');
    loginBtn.onclick = () => {
      console.log("Nút đăng nhập đã được nhấn!");
      formLogin();
    };
    let logoutBtn = document.getElementById('logout-icon');
    logoutBtn.onclick = () =>{
      console.log("da dang xuat va xoa token");
      logout();
      console.log('token ', localStorage.getItem('token'));
    }
  });
}

start();
