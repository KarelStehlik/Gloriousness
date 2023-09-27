@echo off
cd assets

echo ------ > ..\build_log.txt
echo assets images: >> ..\build_log.txt

python main.py >> ..\build_log.txt
cd ..
echo ------ >> build_log.txt
echo HardcodeStats.py >> build_log.txt
python HardcodeStats.py >> build_log.txt
PAUSE