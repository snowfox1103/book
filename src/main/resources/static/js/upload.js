async function uploadToServer(formObj) {
    console.log("Upload to server ........");
    console.log(formObj); //업로드할 폼 데이터 출력
    const response = await axios({ //axios를 이용하여 서버에 파일 업로드 요청
        method: 'post', //Post 요청
        url: '/upload', //업로드 api 엔드포인트
        data: formObj, //업로드할 데이터 (formDbj 객체)
        headers: {
            'Content-Type': 'multipart/form-data', //파일 업로드를 위한 헤더 설정
        },
    });

    return response.data;
}

async function removeFileToServer(uuid, fileName) {
    const response = await axios.delete(`/remove/${uuid}_${fileName}`);

    return response.data;
}