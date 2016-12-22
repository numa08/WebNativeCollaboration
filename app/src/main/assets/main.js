
var content = document.getElementById("content");
content.innerHTML = "hello";

window.userRepository.findUserById(1, (function(err, data) {
                                           if (err) {
                                             console.error("loadUser failed", err);
                                           }
                                           var user = JSON.parse(data);
                                           content.innerHTML = "user id = " + user.id + " user name = " + user.name;
                                       }).toString()
                                       );