import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import javax.sound.sampled.*;

class TeamProject extends JFrame implements KeyListener {
	Image img1, img2, img3, Bulletimg, EnemyImg, fakeimg;
	int key[] = { 0, 0, 0, 0, 0 };
	Point pos = new Point(190, 830);
	Dimension size;
	Graphics fake;

	ArrayList<Bullet> bullet = new ArrayList<Bullet>();
	ArrayList<Enemy> enemy = new ArrayList<Enemy>();
	ArrayList<Team> team = new ArrayList<Team>();
	int BulletReprint = 1;// reprint할건가 안할건가
	int life = 10;// 목숨
	Random random = new Random();
	long speed = 100;// 게임 속도조절
	int kill = 0;

	public static void main(String args[]) {
		new TeamProject();
	}

	public TeamProject() {
		super("testing");
		setResizable(false);

		img1 = getToolkit().getImage("비행기.png");// 이미지받기
		img2 = getToolkit().getImage("비행기2.png");
		img3 = getToolkit().getImage("비행기3.png");

		Bulletimg = getToolkit().getImage("아군총알.png");
		EnemyImg = getToolkit().getImage("적1.png");

		addKeyListener(this);// 플레이속도
		PlayTime playtime = new PlayTime();
		Thread time1 = new Thread(playtime);
		time1.start();
		// ----------------
		Enemyime1 MakeEnemy = new Enemyime1();// 적만드는시간
		Thread makeenemy = new Thread(MakeEnemy);
		makeenemy.start();

		Enemyime2 MakeMove = new Enemyime2();// 적움직이는 시간
		Thread makemove = new Thread(MakeMove);
		makemove.start();
		Music music2 = new Music();// 노래틀기
		Thread music = new Thread(music2);//
		music.start();
		// ----------------------
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 900);
		setBackground(Color.gray);

		setVisible(true);
		size = getSize();
		fakeimg = createImage(380, 900);// 임의의 이미지만들기

	}

	class PlayTime implements Runnable {
		public void run() {
			long nowTime, drawTime;

			nowTime = System.currentTimeMillis();// 현제시간 받기

			drawTime = nowTime + 500;
			while (true) {
				nowTime = System.currentTimeMillis();

				if (drawTime < nowTime) {
					drawTime = nowTime + 10;// 0.1초마다 받기

					if (action() || BulletReprint == 1) {// do move or reprint they do reprint
						BulletReprint = 0;// 0으로 바꾸고 reprint
						repaint();
					}

				}
				if(life<0)//hp0이라면 종료
				{
					break;
				}
			}
		}
	}

	// --------------
	class Enemyime1 implements Runnable {
		public void run() {
			long nowTime, drawTime;

			nowTime = System.currentTimeMillis();
			drawTime = nowTime + 2000;

			while (true) {
				nowTime = System.currentTimeMillis();
				if (drawTime < nowTime) {
					drawTime = nowTime + 1000;// 10초마다 적생성

					enemy.add(new Enemy(random.nextInt(310) + 10, 35, 10));
					BulletReprint = 1;// reprint 하라고하기
					if(life<0)//hp0이라면 종료
					{
						break;
					}

				}
			}
		}
	}

	class Enemyime2 implements Runnable {
		public void run() {
			long nowTime, drawTime;

			nowTime = System.currentTimeMillis();
			drawTime = nowTime + 2000;
			long how = drawTime;
			while (true) {
				nowTime = System.currentTimeMillis();
				if (drawTime < nowTime) {
					drawTime = nowTime + speed;// speed마다 움직이게하기
					if (how + 10000 < nowTime) {// 10초지날때마다 -20
						how = nowTime;
						if (speed < 40) {
							if (speed < 10) {// 속도가 20되면 -5씩
							} else {
								speed -= 5;
							}
						} else {
							speed -= 20;
						}
					}
					for (int i = 0; i < enemy.size(); i++) {// 좌표값바꾸고 reprint하게하기
						Enemy enemy2 = enemy.get(i);
						enemy2.pos.y += 4;
					}

					BulletReprint = 1;

				}
				if(life<0)//hp0이라면 종료
				{
					break;
				}
			}
		}
	}

	class Music implements Runnable {// http://blog.naver.com/PostView.nhn?blogId=helloworld8&logNo=220076589506
		public void run() {
			
				try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(new File("Alan Walker - Fade [NCS Release].wav"));//파일 이름넣기
				Clip clip =AudioSystem.getClip();
				clip.stop();
				clip.open(ais);
				clip.start();
			}catch(Exception e) {}
				
		
		}

	}

	// -----------------------
	public void paint(Graphics g) {

		if (fakeimg == null) {
			return;
		}

		fake = fakeimg.getGraphics();
		if (fake == null) {
			return;
		}
		fake.fillRect(0, 0, size.width, size.height);// 잔상지우기
		fake.setColor(Color.black);// 배경 검은색

		fake.drawImage(img3, pos.x, pos.y, this);// 유저

		fake.setColor(Color.white);// 하얀글씨 아래글,위치
		fake.drawString("방향키=이동       스페이스바=공격", 100, 400);
		fake.setColor(Color.white);// 하얀글씨 아래글,위치
		String life2 = Integer.toString(life);
		fake.drawString("life: " + life2, 320, 60);
		fake.setColor(Color.white);// 하얀글씨 아래글,위치
		String speed2 = Long.toString(speed);
		fake.drawString("seed:" + speed2, 260, 60);
		fake.setColor(Color.white);// 하얀글씨 아래글,위치
		String kill2 = Long.toString(kill);
		fake.drawString("kill:" + kill2, 220, 60);

		// ------------------

		for (int i = 0; i < enemy.size(); i++) {// 적만큼 for문
			Enemy enemy2 = enemy.get(i); // 적 따로따로 생성

			if (enemy2.pos.y >= 900) {// 위치가 창밖이면 삭제
				enemy.remove(i);
				life -= 1;
			}
			if (pos.y - 15 <= enemy2.pos.y + 35 && pos.y + 35 >= enemy2.pos.y - 15)// 유저와 부딪히면 hp깍고 삭제
				if (pos.x - 10 <= enemy2.pos.x + 40 && pos.x + 40 >= enemy2.pos.x - 10) {
					enemy.remove(i);
					life -= 1;
				}

			for (int j = 0; j < bullet.size(); j++) {// 총알만큼 for문
				Bullet bullet2 = bullet.get(j);

				bullet2.pos.y -= 15;// 위치 바꾸기

				if ((bullet2.pos.y <= 0)) {// 창벗어나면 삭제
					bullet.remove(j);
				}

				if (enemy2.pos.y - 35 <= bullet2.pos.y && enemy2.pos.y + 60 >= bullet2.pos.y) {// 적과 부딪히면 적hp깍고 총알 삭제
					if (enemy2.pos.x - 10 <= bullet2.pos.x && enemy2.pos.x + 60 >= bullet2.pos.x) {
						enemy2.HP -= 1;
						bullet.remove(j);
						if (enemy2.HP < 0) {// hp0이하면 삭제하고 킬수올리기
							enemy.remove(i);
							kill++;
						}
						break;
					}
				}
				fake.drawImage(Bulletimg, bullet2.pos.x, bullet2.pos.y, this);// 총알 그리기
			}
			fake.drawImage(EnemyImg, enemy2.pos.x, enemy2.pos.y, this);// 적그리기

		}
		for (int i = 0; i < team.size(); i++) {// 아군 가져오기
			Team team2 = team.get(i);
			if (team2.Img.equals("img1")) {// 이미지에따라 다른그림 그리기
				fake.drawImage(img1, team2.pos.x, team2.pos.y, this);
			} else if (team2.Img.equals("img2")) {
				fake.drawImage(img2, team2.pos.x, team2.pos.y, this);
			} else if (team2.Img.equals("img3")) {
				fake.drawImage(img3, team2.pos.x, team2.pos.y, this);
			}
		}

		g.drawImage(fakeimg, 0, 0, this);// gui창에 덮어쓰기

		// ---------------------

		// -======================

	}

	public boolean action() {
		if (key[0] == 1) {
			if (pos.y <= 35) {
			}

			else {

				pos.y -= 4;
				System.out.println("x:" + pos.x + "y:" + pos.y);

			}
			if (key[4] == 1) {
				if (bullet.size() > 2) {
				} else {
					bullet.add(new Bullet(pos.x + 25, pos.y - 10));
					BulletReprint = 1;
				}
			}
			return true;
		}
		if (key[1] == 1) {
			if (pos.x >= 320) {
			} else {

				pos.x += 4;
				System.out.println("x:" + pos.x + "y:" + pos.y);

			}
			if (key[4] == 1) {
				if (bullet.size() > 2) {
				} else {
					bullet.add(new Bullet(pos.x + 25, pos.y - 10));
					BulletReprint = 1;
				}
			}
			return true;
		}
		if (key[2] == 1) {
			if (pos.y >= 840) {
			} else {

				pos.y += 4;
				System.out.println("x:" + pos.x + "y:" + pos.y);

			}
			if (key[4] == 1) {
				if (bullet.size() > 2) {
				} else {
					bullet.add(new Bullet(pos.x + 25, pos.y - 10));
					BulletReprint = 1;
				}
			}
			return true;
		}
		if (key[3] == 1) {
			if (pos.x <= 10) {
			}

			else {

				pos.x -= 4;
				System.out.println("x:" + pos.x + "y:" + pos.y);

			}
			if (key[4] == 1) {
				if (bullet.size() > 2) {
				} else {
					bullet.add(new Bullet(pos.x + 25, pos.y - 10));
					BulletReprint = 1;
				}
			}
			return true;
		}
		if (key[4] == 1) {
			if (bullet.size() > 2) {
			} else {
				bullet.add(new Bullet(pos.x + 25, pos.y - 10));
				BulletReprint = 1;
			}
			return true;
		}

		return false;
	}

	class Bullet {
		Point pos;

		public Bullet(int x, int y) {
			this.pos = new Point(x, y);
		}
	}

	class Enemy {
		Point pos;
		int HP;

		public Enemy(int x, int y, int HP) {
			this.pos = new Point(x, y);
			this.HP = HP;
		}

	}

	class Team {
		Point pos;
		String Img;

		public Team(String Img, int x, int y) {
			this.pos = new Point(x, y);
			this.Img = Img;
		}

	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			key[0] = 1;
			System.out.println("위");
			break;
		case KeyEvent.VK_RIGHT:
			key[1] = 1;
			System.out.println("오른쪽");
			break;
		case KeyEvent.VK_DOWN:
			key[2] = 1;
			System.out.println("아래");
			break;
		case KeyEvent.VK_LEFT:
			key[3] = 1;
			System.out.println("왼쪽");
			break;
		case KeyEvent.VK_SPACE:
			key[4] = 1;
			System.out.println("총알");
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			key[0] = 0;
			break;
		case KeyEvent.VK_RIGHT:
			key[1] = 0;
			break;
		case KeyEvent.VK_DOWN:
			key[2] = 0;
			break;
		case KeyEvent.VK_LEFT:
			key[3] = 0;
			break;
		case KeyEvent.VK_SPACE:
			key[4] = 0;

			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

}