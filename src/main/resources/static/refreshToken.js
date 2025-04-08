const ipConfigRefresh = {
    BASE_URL: '192.168.88.175'
};

function refreshToken(){
    const urlRefreshToken = 'https://' + ipConfigRefresh.BASE_URL + ':8080/authentication/refresh';
    let token = localStorage.getItem("token");
    let data = {
        "token": token
    }
    let expired = localStorage.getItem("expiredTime");
    let now = Date.now();
    if(parseInt(expired - now) < (300 * 1000)){
        fetch(urlRefreshToken,{
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
        .then((response) => {
            return response.json();
        })
        .then((response) => {
            console.log('token cu: ' + localStorage.getItem("token"));
            localStorage.removeItem("token");
            localStorage.removeItem("expiredTime");
            console.log('da refresh token moi');
            localStorage.setItem("token", response.token);
            const now = Date.now();
            const expired = now + 3600 * 1000;
            localStorage.setItem("expiredTime", expired);
            console.log('token moi: ' + localStorage.getItem("token"));
        })
        .catch(error => console.error("loi khi refresh token ", error));
    }
    else{
        console.log('token con han: ', parseInt(expired - now), 's');
    }
}

setInterval(refreshToken, (240 * 1000))
