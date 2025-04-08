const ipConfigMyinfor = {
    BASE_URL: '192.168.88.175'
};

function myinfor(){
    const urlMyInfo = 'https://' + ipConfigLogin.BASE_URL + ':8080/users/myinfo';
    fetch(urlMyInfo, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            }
        })
        .then(response => response.json())
        .then(data => {
            document.getElementById("username-myinfor").textContent = data.username;
            document.getElementById("fullname-myinfor").textContent = data.fullname;
            document.getElementById("status-myinfor").textContent = "Status: " + data.status;
            document.getElementById("avatar-myinfor").src = "/images/avt.jpg";
        })
        .catch(error => console.error("loi get my info: ", error));
}

document.addEventListener("DOMContentLoaded", function () {
    let myInfor = document.getElementById("myinfor");
    let showBtn = document.querySelector(".information");
    let closeBtn = document.querySelector(".close-btn");

    // Hiển thị thông tin khi nhấn nút
    showBtn.addEventListener("click", function () {
        myinfor();
        myInfor.style.display = 'flex';
    });

    // Đóng hộp thoại
    closeBtn.addEventListener("click", function () {
        myInfor.style.display = 'none';
    });

    // Ẩn myinfor khi nhấn ra ngoài
    myInfor.addEventListener("click", function (event) {
        if (event.target === myInfor) {
            myInfor.style.display = 'none';
        }
    });
});