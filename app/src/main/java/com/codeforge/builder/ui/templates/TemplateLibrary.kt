package com.codeforge.builder.ui.templates

object TemplateLibrary {

    fun getAllTemplates(): List<AppTemplate> = listOf(
        // ── HTML Templates ────────────────────────────────────
        blankHtml(), calculatorTemplate(), todoTemplate(),
        quizTemplate(), clockTemplate(), weatherTemplate(),

        // ── Kotlin Templates ──────────────────────────────────
        blankKotlin(), counterKotlin(), notepadKotlin(),

        // ── Java Templates ────────────────────────────────────
        blankJava(), helloJava()
    )

    // ── Blank HTML ────────────────────────────────────────────
    private fun blankHtml() = AppTemplate(
        id = 1, name = "Blank HTML App", type = "HTML/CSS/JS",
        description = "Empty HTML/CSS/JS starter", category = "Starter",
        colorHex = "#FF6B35",
        files = listOf(
            TemplateFile("index.html", "html", BLANK_HTML, true),
            TemplateFile("style.css", "css", BLANK_CSS),
            TemplateFile("app.js", "javascript", BLANK_JS)
        )
    )

    // ── Calculator ────────────────────────────────────────────
    private fun calculatorTemplate() = AppTemplate(
        id = 2, name = "Calculator", type = "HTML/CSS/JS",
        description = "Functional calculator with history", category = "Utility",
        colorHex = "#2196F3",
        files = listOf(
            TemplateFile("index.html", "html", CALCULATOR_HTML, true),
            TemplateFile("style.css", "css", CALCULATOR_CSS),
            TemplateFile("app.js", "javascript", CALCULATOR_JS)
        )
    )

    // ── Todo List ─────────────────────────────────────────────
    private fun todoTemplate() = AppTemplate(
        id = 3, name = "Todo List", type = "HTML/CSS/JS",
        description = "Task manager with localStorage", category = "Productivity",
        colorHex = "#4CAF50",
        files = listOf(
            TemplateFile("index.html", "html", TODO_HTML, true),
            TemplateFile("style.css", "css", TODO_CSS),
            TemplateFile("app.js", "javascript", TODO_JS)
        )
    )

    // ── Quiz App ──────────────────────────────────────────────
    private fun quizTemplate() = AppTemplate(
        id = 4, name = "Quiz App", type = "HTML/CSS/JS",
        description = "Multiple choice quiz with scoring", category = "Game",
        colorHex = "#9C27B0",
        files = listOf(
            TemplateFile("index.html", "html", QUIZ_HTML, true),
            TemplateFile("style.css", "css", QUIZ_CSS),
            TemplateFile("app.js", "javascript", QUIZ_JS)
        )
    )

    // ── Clock ─────────────────────────────────────────────────
    private fun clockTemplate() = AppTemplate(
        id = 5, name = "Digital Clock", type = "HTML/CSS/JS",
        description = "Real-time clock with stopwatch", category = "Utility",
        colorHex = "#00BCD4",
        files = listOf(
            TemplateFile("index.html", "html", CLOCK_HTML, true),
            TemplateFile("style.css", "css", CLOCK_CSS),
            TemplateFile("app.js", "javascript", CLOCK_JS)
        )
    )

    // ── Weather ───────────────────────────────────────────────
    private fun weatherTemplate() = AppTemplate(
        id = 6, name = "Weather App", type = "HTML/CSS/JS",
        description = "Weather UI template (add your API key)", category = "Utility",
        colorHex = "#FF9800",
        files = listOf(
            TemplateFile("index.html", "html", WEATHER_HTML, true),
            TemplateFile("style.css", "css", WEATHER_CSS),
            TemplateFile("app.js", "javascript", WEATHER_JS)
        )
    )

    // ── Blank Kotlin ──────────────────────────────────────────
    private fun blankKotlin() = AppTemplate(
        id = 7, name = "Blank Kotlin App", type = "Kotlin",
        description = "Empty Kotlin Android starter", category = "Starter",
        colorHex = "#7F52FF",
        files = listOf(TemplateFile("MainActivity.kt", "kotlin", BLANK_KOTLIN, true))
    )

    // ── Counter Kotlin ────────────────────────────────────────
    private fun counterKotlin() = AppTemplate(
        id = 8, name = "Counter App", type = "Kotlin",
        description = "Simple counter with animation", category = "Starter",
        colorHex = "#E91E63",
        files = listOf(TemplateFile("MainActivity.kt", "kotlin", COUNTER_KOTLIN, true))
    )

    // ── Notepad Kotlin ────────────────────────────────────────
    private fun notepadKotlin() = AppTemplate(
        id = 9, name = "Notepad", type = "Kotlin",
        description = "Simple text editor with save", category = "Productivity",
        colorHex = "#607D8B",
        files = listOf(TemplateFile("MainActivity.kt", "kotlin", NOTEPAD_KOTLIN, true))
    )

    // ── Blank Java ────────────────────────────────────────────
    private fun blankJava() = AppTemplate(
        id = 10, name = "Blank Java App", type = "Java",
        description = "Empty Java Android starter", category = "Starter",
        colorHex = "#F44336",
        files = listOf(TemplateFile("MainActivity.java", "java", BLANK_JAVA, true))
    )

    // ── Hello Java ────────────────────────────────────────────
    private fun helloJava() = AppTemplate(
        id = 11, name = "Hello World Java", type = "Java",
        description = "Hello World with click animation", category = "Starter",
        colorHex = "#FF5722",
        files = listOf(TemplateFile("MainActivity.java", "java", HELLO_JAVA, true))
    )

    // ═══════════════ TEMPLATE CONTENT ═══════════════════════

    private val BLANK_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>My App</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
  <div class="container">
    <h1>My App</h1>
    <p>Start editing to build something amazing!</p>
  </div>
  <script src="app.js"></script>
</body>
</html>"""

    private val BLANK_CSS = """* { margin: 0; padding: 0; box-sizing: border-box; }
body {
  font-family: sans-serif; background: #121212;
  color: #fff; display: flex; justify-content: center;
  align-items: center; min-height: 100vh; padding: 24px;
}
.container { text-align: center; }
h1 { font-size: 2rem; color: #BB86FC; margin-bottom: 12px; }
p { color: #9E9E9E; }"""

    private val BLANK_JS = """// Your JavaScript code here
console.log('App loaded!');"""

    private val CALCULATOR_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Calculator</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="calculator">
  <div class="display">
    <div class="history" id="history"></div>
    <div class="current" id="display">0</div>
  </div>
  <div class="buttons">
    <button class="btn wide" onclick="clearAll()">AC</button>
    <button class="btn" onclick="toggleSign()">+/-</button>
    <button class="btn" onclick="percentage()">%</button>
    <button class="btn op" onclick="op('/')">÷</button>
    <button class="btn" onclick="num('7')">7</button>
    <button class="btn" onclick="num('8')">8</button>
    <button class="btn" onclick="num('9')">9</button>
    <button class="btn op" onclick="op('*')">×</button>
    <button class="btn" onclick="num('4')">4</button>
    <button class="btn" onclick="num('5')">5</button>
    <button class="btn" onclick="num('6')">6</button>
    <button class="btn op" onclick="op('-')">−</button>
    <button class="btn" onclick="num('1')">1</button>
    <button class="btn" onclick="num('2')">2</button>
    <button class="btn" onclick="num('3')">3</button>
    <button class="btn op" onclick="op('+')">+</button>
    <button class="btn wide" onclick="num('0')">0</button>
    <button class="btn" onclick="dot()">.</button>
    <button class="btn eq" onclick="equals()">=</button>
  </div>
</div>
<script src="app.js"></script>
</body>
</html>"""

    private val CALCULATOR_CSS = """* { margin:0;padding:0;box-sizing:border-box; }
body { background:#1c1c1e;display:flex;justify-content:center;align-items:center;min-height:100vh; }
.calculator { background:#1c1c1e;border-radius:20px;padding:20px;width:320px; }
.display { background:#1c1c1e;text-align:right;padding:12px 16px;margin-bottom:12px; }
.history { color:#888;font-size:1rem;min-height:24px; }
.current { color:#fff;font-size:3rem;font-weight:300;overflow:hidden;text-overflow:ellipsis; }
.buttons { display:grid;grid-template-columns:repeat(4,1fr);gap:12px; }
.btn { background:#333;color:#fff;border:none;border-radius:50%;width:68px;height:68px;font-size:1.4rem;cursor:pointer;transition:opacity .1s; }
.btn:active { opacity:0.6; }
.btn.op { background:#ff9f0a;color:#fff; }
.btn.eq { background:#ff9f0a;color:#fff; }
.btn.wide { grid-column:span 2;border-radius:34px;width:100%;text-align:left;padding-left:24px; }"""

    private val CALCULATOR_JS = """let current='0', prev='', operator='', newInput=true;
const d=()=>document.getElementById('display');
const h=()=>document.getElementById('history');
function update(v){ d().textContent=v; }
function num(n){ if(newInput){current=n;newInput=false;}else{ current=current==='0'?n:current+n; } update(current); }
function dot(){ if(newInput){current='0.';newInput=false;}else if(!current.includes('.'))current+='.'; update(current); }
function op(o){ if(operator&&!newInput)equals();prev=current;operator=o;newInput=true;h().textContent=prev+' '+o; }
function equals(){
  if(!operator||!prev)return;
  let r=0,a=parseFloat(prev),b=parseFloat(current);
  if(operator==='+')r=a+b;else if(operator==='-')r=a-b;else if(operator==='*')r=a*b;else if(operator==='/')r=b!==0?a/b:'Error';
  h().textContent=prev+' '+operator+' '+current+' =';
  current=String(parseFloat(r.toFixed(10)));operator='';newInput=true;update(current);
}
function clearAll(){current='0';prev='';operator='';newInput=true;update('0');h().textContent='';}
function toggleSign(){current=String(-parseFloat(current));update(current);}
function percentage(){current=String(parseFloat(current)/100);update(current);}"""

    private val TODO_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Todo</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="app">
  <h1>My Tasks</h1>
  <div class="input-row">
    <input type="text" id="taskInput" placeholder="Add a task..." onkeydown="if(event.key==='Enter')addTask()">
    <button onclick="addTask()">+</button>
  </div>
  <div id="stats"></div>
  <ul id="taskList"></ul>
</div>
<script src="app.js"></script>
</body>
</html>"""

    private val TODO_CSS = """* { margin:0;padding:0;box-sizing:border-box; }
body { font-family:sans-serif;background:#121212;color:#fff;min-height:100vh;padding:24px; }
.app { max-width:420px;margin:0 auto; }
h1 { font-size:2rem;color:#BB86FC;margin-bottom:24px; }
.input-row { display:flex;gap:12px;margin-bottom:12px; }
input { flex:1;padding:12px 16px;border-radius:12px;border:1px solid #333;background:#1e1e1e;color:#fff;font-size:1rem; }
button { padding:12px 20px;background:#BB86FC;color:#000;border:none;border-radius:12px;font-size:1.4rem;cursor:pointer; }
#stats { color:#888;font-size:.9rem;margin-bottom:16px; }
ul { list-style:none; }
li { display:flex;align-items:center;gap:12px;padding:14px 16px;background:#1e1e1e;border-radius:12px;margin-bottom:8px; }
li.done span { text-decoration:line-through;color:#555; }
li span { flex:1;font-size:1rem; }
.del { background:none;border:none;color:#F44336;font-size:1.2rem;cursor:pointer;padding:4px 8px; }
input[type=checkbox] { width:20px;height:20px;accent-color:#BB86FC; }"""

    private val TODO_JS = """let tasks=JSON.parse(localStorage.getItem('tasks')||'[]');
function save(){localStorage.setItem('tasks',JSON.stringify(tasks));}
function render(){
  const ul=document.getElementById('taskList');
  const done=tasks.filter(t=>t.done).length;
  document.getElementById('stats').textContent=done+'/'+tasks.length+' completed';
  ul.innerHTML=tasks.map((t,i)=>`<li class="${'$'}{t.done?'done':''}">
    <input type="checkbox" ${t.done?'checked':''} onchange="toggle(${i})">
    <span>${'$'}{t.text}</span>
    <button class="del" onclick="del(${i})">×</button>
  </li>`).join('');
}
function addTask(){
  const i=document.getElementById('taskInput');
  if(!i.value.trim())return;
  tasks.unshift({text:i.value.trim(),done:false});i.value='';save();render();
}
function toggle(i){tasks[i].done=!tasks[i].done;save();render();}
function del(i){tasks.splice(i,1);save();render();}
render();"""

    private val QUIZ_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Quiz</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="app" id="app">
  <div id="start">
    <h1>Quiz App</h1>
    <p>Test your knowledge!</p>
    <button onclick="startQuiz()">Start Quiz</button>
  </div>
  <div id="quiz" style="display:none">
    <div class="progress"><span id="qNum"></span><span id="score">Score: 0</span></div>
    <div class="question" id="question"></div>
    <div id="options"></div>
    <div id="feedback"></div>
  </div>
  <div id="result" style="display:none">
    <h1 id="finalScore"></h1>
    <p id="finalMsg"></p>
    <button onclick="startQuiz()">Play Again</button>
  </div>
</div>
<script src="app.js"></script>
</body>
</html>"""

    private val QUIZ_CSS = """* { margin:0;padding:0;box-sizing:border-box; }
body { font-family:sans-serif;background:#1a1a2e;color:#eee;display:flex;justify-content:center;align-items:center;min-height:100vh;padding:20px; }
.app { width:100%;max-width:400px;text-align:center; }
h1 { font-size:2rem;color:#e94560;margin-bottom:12px; }
p { color:#aaa;margin-bottom:24px; }
button { background:#e94560;color:#fff;border:none;padding:14px 32px;border-radius:12px;font-size:1rem;cursor:pointer;margin:8px; }
.progress { display:flex;justify-content:space-between;margin-bottom:20px;color:#aaa; }
.question { font-size:1.2rem;font-weight:600;margin-bottom:20px;line-height:1.5; }
.opt { display:block;width:100%;padding:14px;margin:8px 0;background:#16213e;border:2px solid #0f3460;border-radius:12px;cursor:pointer;text-align:left;font-size:1rem;color:#eee;transition:all .2s; }
.opt.correct { background:#1b5e20;border-color:#4CAF50; }
.opt.wrong { background:#b71c1c;border-color:#F44336; }
#feedback { font-size:1.1rem;min-height:30px;margin-top:12px; }"""

    private val QUIZ_JS = """const questions=[
  {q:"What is 7 × 8?",options:["54","56","64","48"],ans:1},
  {q:"Capital of Bangladesh?",options:["Chittagong","Rajshahi","Dhaka","Sylhet"],ans:2},
  {q:"HTML stands for?",options:["Hyper Text Markup Language","High Tech ML","Hyper Tool ML","None"],ans:0},
  {q:"Which planet is largest?",options:["Earth","Saturn","Jupiter","Neptune"],ans:2},
  {q:"2^10 = ?",options:["512","1024","2048","256"],ans:1}
];
let cur=0,score=0;
function startQuiz(){cur=0;score=0;document.getElementById('start').style.display='none';document.getElementById('result').style.display='none';document.getElementById('quiz').style.display='block';showQ();}
function showQ(){
  const q=questions[cur];
  document.getElementById('qNum').textContent='Q '+(cur+1)+'/'+questions.length;
  document.getElementById('score').textContent='Score: '+score;
  document.getElementById('question').textContent=q.q;
  document.getElementById('feedback').textContent='';
  document.getElementById('options').innerHTML=q.options.map((o,i)=>`<button class="opt" onclick="answer(${i})">${o}</button>`).join('');
}
function answer(i){
  const q=questions[cur];const btns=document.querySelectorAll('.opt');
  btns.forEach(b=>b.disabled=true);
  if(i===q.ans){score++;btns[i].classList.add('correct');document.getElementById('feedback').textContent='✅ Correct!';}
  else{btns[i].classList.add('wrong');btns[q.ans].classList.add('correct');document.getElementById('feedback').textContent='❌ Wrong!';}
  cur++;setTimeout(()=>{if(cur<questions.length)showQ();else showResult();},1200);
}
function showResult(){
  document.getElementById('quiz').style.display='none';document.getElementById('result').style.display='block';
  document.getElementById('finalScore').textContent=score+'/'+questions.length;
  document.getElementById('finalMsg').textContent=score>=4?'🎉 Excellent!':score>=2?'👍 Good job!':'📚 Keep practicing!';
}"""

    private val CLOCK_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Clock</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="app">
  <div class="clock" id="clock">00:00:00</div>
  <div class="date" id="date"></div>
  <div class="stopwatch">
    <div id="sw">00:00.0</div>
    <div class="sw-btns">
      <button id="startBtn" onclick="swToggle()">Start</button>
      <button onclick="swReset()">Reset</button>
    </div>
  </div>
</div>
<script src="app.js"></script>
</body>
</html>"""

    private val CLOCK_CSS = """* { margin:0;padding:0;box-sizing:border-box; }
body { font-family:'Courier New',monospace;background:#0a0a0a;color:#00ff88;display:flex;justify-content:center;align-items:center;min-height:100vh; }
.app { text-align:center; }
.clock { font-size:4rem;font-weight:700;letter-spacing:4px;text-shadow:0 0 20px #00ff88;margin-bottom:8px; }
.date { color:#888;font-size:1rem;margin-bottom:40px; }
.stopwatch { border:1px solid #00ff8840;border-radius:16px;padding:24px; }
#sw { font-size:2.5rem;letter-spacing:2px;color:#00ccff;text-shadow:0 0 10px #00ccff;margin-bottom:16px; }
.sw-btns { display:flex;gap:16px;justify-content:center; }
button { background:transparent;border:1px solid #00ff88;color:#00ff88;padding:10px 28px;border-radius:8px;font-size:1rem;cursor:pointer; }
button:hover { background:#00ff8820; }"""

    private val CLOCK_JS = """function updateClock(){
  const now=new Date();
  const t=[now.getHours(),now.getMinutes(),now.getSeconds()].map(n=>String(n).padStart(2,'0')).join(':');
  document.getElementById('clock').textContent=t;
  document.getElementById('date').textContent=now.toDateString();
}
setInterval(updateClock,1000);updateClock();
let swRunning=false,swStart=0,swElapsed=0,swInterval;
function swToggle(){
  if(swRunning){clearInterval(swInterval);swElapsed+=Date.now()-swStart;swRunning=false;document.getElementById('startBtn').textContent='Start';}
  else{swStart=Date.now();swInterval=setInterval(updateSw,100);swRunning=true;document.getElementById('startBtn').textContent='Stop';}
}
function updateSw(){
  const ms=swElapsed+(Date.now()-swStart);
  const mins=Math.floor(ms/60000),secs=Math.floor((ms%60000)/1000),ds=Math.floor((ms%1000)/100);
  document.getElementById('sw').textContent=String(mins).padStart(2,'0')+':'+String(secs).padStart(2,'0')+'.'+ds;
}
function swReset(){clearInterval(swInterval);swRunning=false;swElapsed=0;document.getElementById('sw').textContent='00:00.0';document.getElementById('startBtn').textContent='Start';}"""

    private val WEATHER_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>Weather</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="app">
  <div class="search">
    <input type="text" id="cityInput" placeholder="Enter city name...">
    <button onclick="getWeather()">Search</button>
  </div>
  <div id="weatherCard" style="display:none">
    <div class="city" id="city">City</div>
    <div class="temp" id="temp">--°C</div>
    <div class="desc" id="desc">--</div>
    <div class="details">
      <div><span>Humidity</span><b id="humidity">--%</b></div>
      <div><span>Wind</span><b id="wind">-- km/h</b></div>
    </div>
  </div>
  <div id="noKey" style="display:none;color:#F44336;margin-top:20px">
    Add your OpenWeatherMap API key in app.js
  </div>
</div>
<script src="app.js"></script>
</body>
</html>"""

    private val WEATHER_CSS = """* { margin:0;padding:0;box-sizing:border-box; }
body { font-family:sans-serif;background:linear-gradient(135deg,#1a1a2e,#16213e);color:#eee;min-height:100vh;display:flex;justify-content:center;align-items:flex-start;padding:40px 20px; }
.app { width:100%;max-width:380px; }
.search { display:flex;gap:8px;margin-bottom:24px; }
input { flex:1;padding:12px;border-radius:12px;border:1px solid #ffffff20;background:#ffffff10;color:#fff;font-size:1rem; }
button { background:#4fc3f7;border:none;color:#000;padding:12px 20px;border-radius:12px;cursor:pointer;font-size:1rem; }
#weatherCard { background:#ffffff10;border-radius:20px;padding:32px;text-align:center;backdrop-filter:blur(10px); }
.city { font-size:1.5rem;margin-bottom:8px; }
.temp { font-size:4rem;font-weight:300;color:#4fc3f7; }
.desc { text-transform:capitalize;color:#aaa;margin-bottom:24px; }
.details { display:flex;justify-content:space-around; }
.details div { text-align:center; }
.details span { color:#888;font-size:.85rem;display:block; }
.details b { font-size:1.1rem; }"""

    private val WEATHER_JS = """const API_KEY = 'YOUR_OPENWEATHERMAP_API_KEY'; // Get from openweathermap.org
async function getWeather(){
  const city=document.getElementById('cityInput').value.trim();
  if(!city)return;
  if(API_KEY==='YOUR_OPENWEATHERMAP_API_KEY'){
    document.getElementById('noKey').style.display='block';return;
  }
  try{
    const r=await fetch(`https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${API_KEY}&units=metric`);
    const d=await r.json();
    document.getElementById('city').textContent=d.name+', '+d.sys.country;
    document.getElementById('temp').textContent=Math.round(d.main.temp)+'°C';
    document.getElementById('desc').textContent=d.weather[0].description;
    document.getElementById('humidity').textContent=d.main.humidity+'%';
    document.getElementById('wind').textContent=Math.round(d.wind.speed*3.6)+' km/h';
    document.getElementById('weatherCard').style.display='block';
  }catch(e){alert('City not found');}
}
document.getElementById('cityInput').addEventListener('keydown',e=>e.key==='Enter'&&getWeather());"""

    private val BLANK_KOTLIN = """package your.package.name

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF121212.toInt())
        }
        val tv = TextView(this).apply {
            text = "Hello, Android!"
            textSize = 24f
            setTextColor(0xFFBB86FC.toInt())
            gravity = Gravity.CENTER
        }
        layout.addView(tv)
        setContentView(layout)
    }
}"""

    private val COUNTER_KOTLIN = """package your.package.name

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    var count = 0
    lateinit var tvCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF0D0D0D.toInt())
            setPadding(48, 48, 48, 48)
        }
        tvCount = TextView(this).apply {
            text = "0"
            textSize = 80f
            setTextColor(0xFFE91E63.toInt())
            gravity = Gravity.CENTER
        }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val btnMinus = MaterialButton(this).apply {
            text = "-"
            textSize = 24f
            setOnClickListener { tvCount.text = (--count).toString() }
        }
        val btnPlus = MaterialButton(this).apply {
            text = "+"
            textSize = 24f
            setOnClickListener { tvCount.text = (++count).toString() }
        }
        val btnReset = MaterialButton(this).apply {
            text = "Reset"
            setOnClickListener { count = 0; tvCount.text = "0" }
        }
        row.addView(btnMinus)
        row.addView(btnPlus)
        root.addView(tvCount)
        root.addView(row)
        root.addView(btnReset)
        setContentView(root)
    }
}"""

    private val NOTEPAD_KOTLIN = """package your.package.name

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("note", Context.MODE_PRIVATE)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1a1a1a.toInt())
            setPadding(16,16,16,16)
        }
        val title = TextView(this).apply {
            text = "Notepad"
            textSize = 22f
            setTextColor(0xFFBB86FC.toInt())
            setPadding(8,8,8,16)
        }
        val et = EditText(this).apply {
            setText(prefs.getString("note",""))
            setTextColor(0xFFEEEEEE.toInt())
            setHintTextColor(0xFF555555.toInt())
            hint = "Start writing..."
            background = null
            textSize = 16f
            gravity = android.view.Gravity.TOP
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val btn = android.widget.Button(this).apply {
            text = "Save"
            setOnClickListener {
                prefs.edit().putString("note", et.text.toString()).apply()
                Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(title)
        layout.addView(et)
        layout.addView(btn)
        setContentView(layout)
    }
}"""

    private val BLANK_JAVA = """package your.package.name;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF121212);
        TextView tv = new TextView(this);
        tv.setText("Hello, Android!");
        tv.setTextSize(24f);
        tv.setTextColor(0xFFBB86FC);
        tv.setGravity(Gravity.CENTER);
        layout.addView(tv);
        setContentView(layout);
    }
}"""

    private val HELLO_JAVA = """package your.package.name;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF0D0D0D);

        TextView tv = new TextView(this);
        tv.setText("Hello, World!");
        tv.setTextSize(32f);
        tv.setTextColor(0xFF4CAF50);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0,0,0,32);

        Button btn = new Button(this);
        btn.setText("Click Me!");
        btn.setOnClickListener(v -> {
            ObjectAnimator a = ObjectAnimator.ofFloat(tv,"scaleX",1f,1.2f,1f);
            a.setDuration(300);
            a.start();
            tv.setText("You clicked! 🎉");
        });
        layout.addView(tv);
        layout.addView(btn);
        setContentView(layout);
    }
}"""
}
