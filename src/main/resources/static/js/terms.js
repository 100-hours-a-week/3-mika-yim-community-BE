document.addEventListener('DOMContentLoaded', () => {
    const nextButton = document.getElementById('next-button');
    const requiredCheckboxes = document.querySelectorAll('input[type="checkbox"][required]');

    const checkAgreements = () => {
        let allAgreed = true;

        for (let i = 0; i < requiredCheckboxes.length; i++) {
            const checkbox = requiredCheckboxes[i];

            // 만약 체크가 안 된 박스를 발견하면, 버튼 비활성화
            if (checkbox.checked === false) {
                allAgreed = false;
                break;
            }
        }
        nextButton.disabled = !allAgreed;
    };

    requiredCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', checkAgreements);
    });

    checkAgreements();

    nextButton.addEventListener('click', () => {
        // 회원가입 페이지로 이동
        window.location.href = '/public/pages/signup/signup.html';
    });
})