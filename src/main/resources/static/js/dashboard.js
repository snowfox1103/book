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
//달력 파트
let dt = new Date();
//달력 테이블 만들기
let cY = dt.getFullYear();
let cM = dt.getMonth()+1;
let firstofMonth = new Date(cY,cM-1,1)
let weekday = firstofMonth.getDay();
let week = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
document.createElement('table');
let week_tr = document.createElement('tr');
for(i=0;i<7;i++){
    let week_td = week_tr.createElement('td');
    week_td.innerHTML = week[i];
}

