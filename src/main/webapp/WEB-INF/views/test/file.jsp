<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
          integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l" crossorigin="anonymous">
    <title>Test - 파일 분할 업로드</title>
</head>
<body>
<h1>TEST</h1>
<input id="video-file" type="file" name="file">
<button onclick="sendVideoChunks()">분할 업로드</button>
<button onclick="sendVideo()">일반 업로드</button>
<div id="result"></div>
<div id="result2"></div>

<script>
    const sendVideoChunks = () => {
        console.time("chunks time taken");
        const chunkSize = 20 * 1024 * 1024; // 1MB
        const file = document.getElementById("video-file").files[0];
        const resultElement = document.getElementById("result");

        // total size 계산
        const totalChunks = Math.ceil(file.size / chunkSize);
        let currentChunk = 0;

        // chunk file 전송
        const sendNextChunk = () => {

            // chunk size 만큼 데이터 분할
            const start = currentChunk * chunkSize;
            const end = Math.min(start + chunkSize, file.size);

            const chunk = file.slice(start, end);

            // form data 형식으로 전송
            const formData = new FormData();
            formData.append("chunk", chunk, file.name);
            formData.append("chunkNumber", currentChunk);
            formData.append("totalChunks", totalChunks);

            fetch("/api/test/upload/big", {
                method: "POST",
                body: formData
            }).then(resp => {
                // 전송 결과가 206이면 다음 파일 조각 전송
                if (resp.status === 206) {
                    // 진행률 표시
                    resultElement.textContent = Math.round(currentChunk / totalChunks * 100) + "%"
                    currentChunk++;
                    if (currentChunk < totalChunks) {
                        sendNextChunk();
                    }
                    // 마지막 파일까지 전송 되면
                } else if (resp.status === 200) {
                    resp.text().then(data => resultElement.textContent = data);
                    console.timeEnd("chunks time taken");
                }
            }).catch(err => {
                console.error("Error uploading video chunk");
            });
        };

        sendNextChunk();
    }

    const sendVideo = () => {
        console.time('default upload')
        const file = document.getElementById("video-file").files[0];
        const resultElement = document.getElementById("result2");

        // total size 계산

        const formData = new FormData();
        formData.append("file", file);

        fetch("/api/test/upload/aws", {
            method: "POST",
            body: formData
        }).then(resp => {
            // 전송 결과가 206이면 다음 파일 조각 전송
            if (resp.status === 200) {
                console.log(resp);
                resp.text().then(data => resultElement.textContent = data);
                console.timeEnd('default upload')
            }
        }).catch(err => {
            console.error("Error uploading video chunk");
        });
    }
</script>
<!-- Optional JavaScript; choose one of the two! -->

<!-- Option 1: jQuery and Bootstrap Bundle (includes Popper) -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
        integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-Piv4xVNRyMGpqkS2by6br4gNJ7DXjqk09RmUpJ8jgGtD7zP9yug3goQfGII0yAns"
        crossorigin="anonymous"></script>

<!-- Option 2: Separate Popper and Bootstrap JS -->
<!--
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js" integrity="sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.min.js" integrity="sha384-+YQ4JLhjyBLPDQt//I+STsc9iw4uQqACwlvpslubQzn4u2UU2UFM80nGisd026JF" crossorigin="anonymous"></script>
-->
</body>
</html>