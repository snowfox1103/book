const ctx = document.getElementById('budgetChart').getContext('2d');
const budgetChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['식비', '여가비', '교통비'],
        datasets: [
            {
                label: '설정 금액',
                data: [250000, 200000, 50000],
                backgroundColor: 'rgba(54, 162, 235, 0.6)'
            },
            {
                label: '남은 금액',
                data: [200000, 150000, 25000],
                backgroundColor: 'rgba(255, 206, 86, 0.6)'
            }
        ]
    },
    options: {
        responsive: false,
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: value => value.toLocaleString() + '원'
                }
            }
        },
        plugins: {
            title: {
                display: true,
                text: '예산 현황 막대그래프',
                font: {
                    size: 20
                }
            }
        }
    }
});
// 도넛그래프
const ctx2 = document.getElementById('categoryChart').getContext('2d');
const categoryChart = new Chart(ctx2, {
    type: 'doughnut',
    data: {
        labels: ['식비', '교통비', '문화/여가', '쇼핑', '기타'],
        datasets: [{
            label: '지출 금액',
            data: [150000, 50000, 30000, 40000, 20000],  // 예시 값 (원 단위)
            backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(153, 102, 255, 0.6)'
            ],
            borderWidth: 1
        }]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: '카테고리별 지출 비율',
                font: {
                    size: 18
                }
            },
            legend: {
                position: 'bottom'
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        let value = context.parsed;
                        return context.label + ': ' + value.toLocaleString() + '원';
                    }
                }
            }
        }
    }
});
// 꺾은 선 그래프 ()
const ctx3 = document.getElementById('monthlyChart').getContext('2d');
const monthlyChart = new Chart(ctx3, {
    type: 'line',
    data: {
        labels: ['4월', '5월', '6월', '7월'],
        datasets: [{
            label: '총 지출액 (원)',
            data: [520000, 480000, 530000, 410000], // 무작위 지출액 데이터
            backgroundColor: 'rgba(255, 159, 64, 0.4)',
            borderColor: 'rgba(255, 99, 132, 1)',
            tension: 0.3,
            fill: true,
            pointBackgroundColor: 'rgba(255, 99, 132, 1)',
            pointRadius: 5,
            pointHoverRadius: 7
        }]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: '최근 4개월 총 지출 추이',
                font: {
                    size: 18
                }
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        return context.dataset.label + ': ' + context.parsed.y.toLocaleString() + '원';
                    }
                }
            },
            legend: {
                display: false
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: value => value.toLocaleString() + '원'
                }
            }
        }
    }
});
let today = new Date();
let currentYear = today.getFullYear();
let currentMonth = today.getMonth(); // 0~11 (1월=0)

const calHeaderYear = document.querySelector(".cal-header h1");
const calHeaderMonth = document.querySelector(".cal-month p");
const calBody = document.querySelector(".cal-body");

function renderCalendar(year, month) {
    // 달력 헤더 변경
    calHeaderYear.textContent = year;
    calHeaderMonth.textContent = (month + 1) + "월";

    // 기존 날짜 지우기
    calBody.innerHTML = "";

    // 요일 헤더
    const week = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
    week.forEach((day, idx) => {
        const div = document.createElement("div");
        div.classList.add("grid-item", "day");
        div.textContent = day;
        if (idx === 0 || idx === 6) div.style.color = "red"; // 주말 빨강
        calBody.appendChild(div);
    });

    // 이번 달 1일 요일
    const firstDay = new Date(year, month, 1).getDay();
    // 이번 달 마지막 날짜
    const lastDate = new Date(year, month + 1, 0).getDate();

    // 빈칸 채우기
    for (let i = 0; i < firstDay; i++) {
        const empty = document.createElement("div");
        empty.classList.add("grid-item");
        calBody.appendChild(empty);
    }

    // 날짜 채우기
    for (let d = 1; d <= lastDate; d++) {
        const dateDiv = document.createElement("div");
        dateDiv.classList.add("grid-item");
        dateDiv.textContent = d;

        // 오늘 날짜 표시
        if (
            year === today.getFullYear() &&
            month === today.getMonth() &&
            d === today.getDate()
        ) {
            dateDiv.style.backgroundColor = "#d1ecf1";
            dateDiv.style.fontWeight = "bold";
        }

        calBody.appendChild(dateDiv);
    }
    // 마지막 줄 남은 칸 채우기
    const totalCells = firstDay + lastDate;
    const remain = 7 - (totalCells % 7);
    if (remain < 7) {
        for (let i = 0; i < remain; i++) {
            const empty = document.createElement("div");
            empty.classList.add("grid-item");
            calBody.appendChild(empty);
        }
    }
}

// 초기 렌더링
renderCalendar(currentYear, currentMonth);

// 버튼 이벤트
document.getElementById("prev-month").addEventListener("click", () => {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    renderCalendar(currentYear, currentMonth);
});

document.getElementById("next-month").addEventListener("click", () => {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    renderCalendar(currentYear, currentMonth);
});