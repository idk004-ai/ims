function updateTime() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    const seconds = now.getSeconds();

    const period = hours >= 12 ? 'PM' : 'AM';
    const hour12 = hours % 12 || 12; // Convert to 12-hour format

    document.getElementById('hours').textContent = hour12.toString().padStart(2, '0');
    document.getElementById('minutes').textContent = minutes.toString().padStart(2, '0');
    document.getElementById('seconds').textContent = seconds.toString().padStart(2, '0');
    document.getElementById('period').textContent = period;

    // Dynamically adjust animation offsets
    const hourCircle = document.getElementById('hour-circle');
    const minuteCircle = document.getElementById('minute-circle');
    const secondCircle = document.getElementById('second-circle');

    hourCircle.style.transform = `rotate(${(hour12 / 12) * 360}deg)`;
    minuteCircle.style.transform = `rotate(${(minutes / 60) * 360}deg)`;
    secondCircle.style.transform = `rotate(${(seconds / 60) * 360}deg)`;
}

setInterval(updateTime, 1000);
updateTime(); // Initial call to set the time immediately



