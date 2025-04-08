const ipConfigMain = {
    BASE_URL: '192.168.88.175'
};

const username = localStorage.getItem('username');
const fullname = localStorage.getItem('fullname');
const token = localStorage.getItem('token');

const inputUsername = document.getElementById('inputUsername');
const targetUsername = inputUsername ? inputUsername.value.trim() : "";

// Kiểm tra xem người dùng có token hợp lệ không
if (!token) {
    let inforIcon = document.querySelector(".information");
    let loginIcon = document.querySelector(".login-icon");
    let logoutIcon = document.getElementById('logout-icon');
    logoutIcon.style.display = 'none';
    inforIcon.style.display = 'none';
    loginIcon.style.display = 'inline-block';
    alert("ban can dang nhap de su dung tinh nang nay.");
} else {
    // Kiểm tra tính hợp lệ của token trước khi mở WebSocket
    const urlCheckToken = 'https://'+ ipConfigMain.BASE_URL + ':8080/authentication/checkToken';
    fetch(urlCheckToken, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ token: token })
    })
    .then((response) => response.json())
    .then((response) => {
        if (!response.authentication) {
            let inforIcon = document.querySelector(".information");
            let loginIcon = document.querySelector(".login-icon");
            let logoutIcon = document.getElementById('logout-icon');
            logoutIcon.style.display = 'none';
            inforIcon.style.display = 'none';
            loginIcon.style.display = 'inline-block';
            alert('token khong hop le.');

        } else {
            let inforIcon = document.querySelector(".information");
            let loginIcon = document.querySelector(".login-icon");
            let logoutIcon = document.getElementById('logout-icon');
            logoutIcon.style.display = 'inline-block';
            inforIcon.style.display = 'inline-block';
            loginIcon.style.display = 'none';

            const websocket = new WebSocket('wss://' + ipConfigMain.BASE_URL + ':8080/socket');

            let peerConnection;
            let localStream;
            let remoteStream;

            let localVideo = document.getElementById('localVideo');
            let remoteVideo = document.getElementById('remoteVideo');

            function send(message) {
                websocket.send(JSON.stringify(message));
            }

            websocket.onopen = function() {
                send({
                    event: "register",
                    sender: username
                });

                const configurations = null;
                peerConnection = new RTCPeerConnection(configurations);

                const constraint = {
                    audio: true,
                    video: true
                };

                navigator.mediaDevices.getUserMedia(constraint)
                    .then((stream) => {
                        localStream = stream;
                        localVideo.srcObject = stream;

                        stream.getTracks().forEach(track => {
                            peerConnection.addTrack(track, stream);
                        });
                    })
                    .catch((error) => {
                        alert("khong the truy cap camera va audio");
                        console.log('Lỗi truy cập thiết bị media: ', error);
                    });

                peerConnection.onicecandidate = function(event) {
                    if (event.candidate) {
                        send({
                            event: "candidate",
                            sender: username,
                            target: targetUsername,
                            data: event.candidate
                        });
                    }
                };

                peerConnection.ontrack = function(event) {
                    if (!remoteStream) {
                        remoteStream = new MediaStream();
                        remoteVideo.srcObject = remoteStream;
                    }
                    remoteStream.addTrack(event.track);
                };
            };

            websocket.onmessage = function(message) {
                const content = JSON.parse(message.data);
                const data = content.data;
                const sender = content.sender;
                if (content.event === "offer") {
                    handleOffer(data, sender);
                }
                else if (content.event === "answer") {
                    handleAnswer(data);
                }
                else if (content.event === "candidate") {
                    handleCandidate(data, sender);
                }
                else if (content.event === "callRejected"){
                    alert(content.sender + " đã từ chối cuộc gọi của bạn.");
                }
            };

            function handleOffer(offer, sender) {
                const formCallBtn = document.querySelector(".formReceiveCall");
                if(formCallBtn){
                    formCallBtn.style.display = "flex";
                }
                document.querySelector('.fullname').textContent = fullname;
                const acceptBtn = document.getElementById('accept');
                const refuseBtn = document.getElementById('refuse');
                accept.onclick = () => {
                    peerConnection.setRemoteDescription(new RTCSessionDescription(offer))
                        .then(() => peerConnection.createAnswer())
                        .then((answer) => peerConnection.setLocalDescription(answer))
                        .then(() => {
                            send({
                                event: "answer",
                                sender: username,
                                target: sender,
                                data: peerConnection.localDescription
                            });
                        })
                        .catch((error) => console.log('Lỗi xử lý offer: ', error));
                    formCallBtn.style.display = "none";
                }
                refuse.onclick = () => {
                    send({
                        event: "reject",
                        sender: username,
                        target: sender
                    });
                    formCallBtn.style.display = "none";
                }
            }

            function handleAnswer(answer) {
                peerConnection.setRemoteDescription(new RTCSessionDescription(answer))
                    .then(() => console.log('Thiết lập kết nối thành công'))
                    .catch((error) => console.log('Lỗi xử lý answer: ', error));
            }

            function handleCandidate(candidate, sender) {
                if (sender !== targetUsername) {
                    console.log("Candidate nhận được không phải từ người gọi");
                    return;
                }

                peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
                    .then(() => console.log('Thêm thành công candidate'))
                    .catch((error) => console.log('Lỗi xử lý candidate: ', error));
            }

            function makeCall() {
                let usernameCall = document.getElementById('inputUsername').value.trim();
                if (!usernameCall) {
                    console.log('Vui lòng nhập tên người nhận cuộc gọi');
                    return;
                }

                console.log(username + " đang gọi tới " + usernameCall);
                peerConnection.createOffer()
                    .then((offer) => peerConnection.setLocalDescription(offer))
                    .then(() => send({
                        event: "offer",
                        sender: username,
                        target: usernameCall,
                        data: peerConnection.localDescription
                    }))
                    .catch((error) => console.log('Lỗi tạo offer: ', error));
            }

            let makeCallBtn = document.getElementById('make-call');
            makeCallBtn.onclick = () => {
                makeCall();
            };
        }
    })
    .catch((error) => {
        console.log('Lỗi khi kiểm tra token:', error);
    });
}
