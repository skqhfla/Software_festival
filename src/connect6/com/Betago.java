package connect6.com;
public class Betago {
   
   //모든 돌이 놓일 때 마다 addWeight 실행시켜주고,
   //AI턴에서는 returnPoint로 받아오기
   
   static int color = DummyAI.getMyColor();
   static int opponent = DummyAI.getYourColor();
   private static int[][] playBoard = new int[19][19];
   
   
   //copy... (얕은복사라 주소지까지 연결된...)
   public static void getBoard(ConnectSix.Board B) {
      playBoard = B.board;
   }
   
   

   //integer 형태의 이상적 좌표를 형식에 맞춘 String으로 바꿔 리턴(다음에 놓을 그거임. 스톤 하나하나 기준.)
   public static String returnStringCoor() {
      
      
      returnPoint();
      //System.out.println("now x and y is "+x+", "+y);
      //x, y를 바탕으로 String형태의 머시깽이...
      String stone1 = String.format("%c%02d", (char)((x<8)?(x+'A'):(x+'A'+1)), y+1);
      
      
      //일단여기는절대두지말라는뜻!!!! board를 직접 수정하면 NOTEMPTY에러가 나서 임시방편으로... 
      weight[x][y] = -100000;
      //방금 자기가 놓은거 업데이트해주고 
      addWeight(x, y);
      

      returnPoint();
      //System.out.println("now x and y is "+x+", "+y);
      String stone2 = String.format("%c%02d", (char)((x<8)?(x+'A'):(x+'A'+1)), y+1);
      
      weight[x][y] = -100000;
      //방금 자기가 놓은거 업데이트해주고 
      showWeight();

      
      String result = stone1 + ":" + stone2;
      //System.out.println(result);
      
      return result;
      
   }
   
   
   
  // 현재 산출된 이상적 좌표
  static int x;
  static int y;
  // 가중치 설정을 위한 배열
  static int[][] weight = new int[19][19]; // 일반가중치(계속 누적)
  static int[][] superWeight = new int[19][19]; // 특수가중치(매 분석마다 리셋후 시작)
  

  
  // 가중치 기본 누적하기 (매 실행 후에)
  public static void addWeight(int x, int y) {
     

      int n = 1; // 누적할 가중치의 양. 내 돌인지 상대 돌인지에 따라 달라짐.

      if (playBoard[x][y] == 1) //black
          n = 2;
      else if (playBoard[x][y] == 2) //white
          n = 1;
      else if (playBoard[x][y] == 3) { // 중립구 취급
          weight[x][y] = -100;
          return;
      } else return; // 이미 놓여진곳 취급
      
      weight[x][y] = -10000;

      // 팔방을 뒤져서 이미 놓여진 곳만 아니라면 가중치 누적.
      for (int i = x - 1; i < x + 2; i++) {
          for (int j = y - 1; j < y + 2; j++) {
              if (i == x && j == y) { // 본인자리에는 -1
                  weight[x][y] = -10000;
              } else {
                  try {
                      if (playBoard[i][j] == 0)
                          weight[i][j] += n;
                  } catch (ArrayIndexOutOfBoundsException e) {
                  } // 인덱스 넘어서면 무시
              }
          }
      }

  }

  
  // 판 읽고 특수가중치 누적하기
  private static void addSuperWeight() {

      // 특수가중치 판 초기화
      for (int i = 0; i < 19; i++) {
          for (int j = 0; j < 19; j++) {
              superWeight[i][j] = 0;
          }
      }

      int myCount = 0, emptyCount = 0, check;
      
      //// 놓으면 이길 때(한방승리)
      //-------------------------------------------------------------------------------------

      
      
      
      // 세로 공격 시작점 ----------------------------------------------------------------------
      for(int i = 0; i < 19; i++) { //0~18
        for(int j = 0; j < 14; j++) { //0~12
           
           myCount = 0;
           emptyCount = 0;
           
          for(int k = 0; k < 6; k++) { //13~18
                if(playBoard[i][j+k] == color) myCount++;
                else if(playBoard[i][j+k] == 0) emptyCount++;
          }
   
           if(!(myCount + emptyCount == 6) || !(emptyCount == 1 || emptyCount == 2)) continue;
           
           //if((j == 0 || j > 0 && playBoard[i][j - 1] != color) && (j + 5 == 18 || j + 5 < 18 && playBoard[i][j + 6] != color)) {
          if((j - 1 >= 0 && playBoard[i][j - 1] != color) && (j + 6 <= 18 && playBoard[i][j + 6] != color)) {
                  
              //System.out.println("세로 공격로 진입. " + i+" : "+j);
              
             for(int k = 0; k < 6; k++) { //13~18
                 if(playBoard[i][j+k] == 0 && weight[i][j+k] >= 0) {
                    superWeight[i][j+k] += 500;
                  //System.out.println("가중치 부여됨 " + i+" : "+j+k);
                   return;
                 }
              }
           }
        }
      }

     
      // 가로 공격 시작점 -------------------------------------------------------------------------------------------------
      for(int j = 0; j < 19; j++) {
       for(int i = 0; i < 14; i++) {
          
          myCount = 0;
          emptyCount = 0;
          
          for(int k = 0; k < 6; k++) {
             if(playBoard[i+k][j] == color) myCount++;
             else if(playBoard[i+k][j] == 0) emptyCount++;
          }
          
         if(!(myCount + emptyCount == 6) || !(emptyCount == 1 || emptyCount == 2)) continue;
          
          
          //if((i == 0 || i > 0 && playBoard[i - 1][j] != color) && (i + 5 == 18 || i + 5 < 18 && playBoard[i + 6][j] != color)) {
         if((i -1 >= 0 && playBoard[i - 1][j] != color) && (i + 6 <= 18 && playBoard[i + 6][j] != color)) {
                  
             //System.out.println("가로 공격로 진입." + i+" : "+j);
            for(int k = 0; k < 6; k++) { //13~18
                   if(playBoard[i+k][j] == 0 && weight[i+k][j] >= 0) {
                     superWeight[i+k][j] += 500;
                  //System.out.println("가중치 부여됨 " + i+" : "+j+k);
                  return;
                   }
            }
          }
       }
      }
       

      
   // 좌대각 공격 시작점 ----------------------------------------------------------------------
      for(int i = 0; i < 14; i++) { //0~13
       for(int j = 0; j < 14; j++) { //0~13
           
           myCount = 0;
           emptyCount = 0;
           
           for(int k = 0; k < 6; k++) { //13~18, 본인부터 본인+5, 왼쪽아래로 내려가니까 좌대각 \
               if(playBoard[i+k][j+k] == color) myCount++;
               else if(playBoard[i+k][j+k] == 0) emptyCount++;
           }

           if(!(myCount + emptyCount == 6) || (emptyCount > 2)) continue;
           
           if(((i-1 >= 0 && j-1 >= 0) && playBoard[i-1][j - 1] != color) && ((i+6 <= 18 && j+6 <= 18) && playBoard[i+6][j+6] != color)) {
               System.out.println("좌대각 공격로 진입." + i+" : "+j);

               for(int k = 0; k < 6; k++) { //빈칸에 가중치 쏴주고 리턴 
                   if(playBoard[i+k][j+k] == 0 && weight[i+k][j+k] >= 0) {
                       superWeight[i+k][j+k] += 500;
                       System.out.println("가중치 부여됨 " + i+" : "+j+k);
                       return;
                   }
               }
           }
       }
   }

   
   // 우대각 공격 시작점 ----------------------------------------------------------------------
   for(int i = 5; i < 19; i++) { //5~18
       for(int j = 0; j < 14; j++) { //0~13
           
           myCount = 0;
           emptyCount = 0;
           
           for(int k = 0; k < 6; k++) { //13~18, 본인부터...오른쪽아래로 내려가니까 우대각 /
               if(playBoard[i-k][j+k] == color) myCount++;
               else if(playBoard[i-k][j+k] == 0) emptyCount++;
           }

           if(!(myCount + emptyCount == 6) || (emptyCount > 2)) continue; 
           
           if(((i+1 <=18&& j-1 >= 0) && playBoard[i+1][j - 1] != color) && ((i-6 >= 0 && j+6 <= 18) && playBoard[i-6][j+6] != color)) {
               for(int k = 0; k < 6; k++) { //빈칸에 가중치 쏴주고 리턴
                   
                   System.out.println("우대각 공격로 진입." + i+" : "+j);
                   
                   if(playBoard[i-k][j+k] == 0 && weight[i-k][j+k] >= 0){
                       superWeight[i-k][j+k] += 500;
                       System.out.println("가중치 부여됨 " + i+" : "+j+k);
                       return;
                   } 
               }
           }
       }
   }

       

      //// 안놓으면 질 때, 한방방어
      //// ----------------------------------------------------------------------------------
       
  
       
      //가로 방어 시작점 ------------------------------------------------------

      
      for (int i = 0; i < 19; i++) { //14넘으면 여섯개 범위 잡을 때 어차피 인덱스 에러 나니깐... 
          for (int j = 0; j < 19; j++) { 
              myCount = 0;
              check = 0;
              //본인기준 여섯개 체크 
              for (int k = 0; k < 6; k++) {
                  try {
                      //상대 돌의 연속점? 세는거같은데 중간에 자기 색이 나오면 세던거 엎어버림. 
                      if (playBoard[i + k][j] == opponent) {
                          myCount++;
                      }
                      else if(playBoard[i + k][j] == color) {
                          myCount = 0;
                          break;
                      }
                      //비어있는부분 만나면 체크를.. 그 비어있는곳 앞에 몇개의 상대돌이 연속되어있는지 저장해주는건가봐. 
                      else if(playBoard[i + k][j] == 0) {
                          if(check == 0 &&  myCount != 0)
                              check = myCount;
                      }
                      
                  } catch (ArrayIndexOutOfBoundsException e) { }
              }


              if(myCount == 5) {
                  try {
                      //연속
                      if(check == 5) {
                          if(playBoard[i][j] == 0 && playBoard[i - 1][j] != opponent) {
                              if(playBoard[i + 6][j] == 0) {
                                  superWeight[i][j] += 500;
                                  superWeight[i + 6][j] += 500;
                                  return;
                              }
                              else if(playBoard[i + 6][j] == color) {
                                  superWeight[i][j] += 500; 
                                  return;
                              }
                          }
                          else if(playBoard[i + 5][j] == 0 && playBoard[i - 6][j] != opponent) {
                              if(playBoard[i - 1][j] == 0) {
                                  superWeight[i - 1][j] += 500;
                                  superWeight[i + 5][j] += 500; 
                                  return;
                              }
                              else if(playBoard[i - 1][j] == color) {
                                  superWeight[i + 5][j] += 500; 
                                  return;
                              }
                          }
                      }


                      //중간에 공백 있음
                      else if(playBoard[i - 1][j] != opponent && playBoard[i + 6][j] != opponent){
                          for(int k = 0; k < 6; k++) {
                              if(playBoard[i + k][j] == 0) {
                                  superWeight[i + k][j] += 500;
                                  return;
                              }
                          }
                      }


                  }
                  catch (ArrayIndexOutOfBoundsException e) { }
              }


              //4연
              else if(myCount == 4) {
                try {

                    if(check == 4) {
                        for(int k = 0; k < 6; k++) {
                            //4개 연속
                            if(playBoard[i ][j + k] == opponent) {
                                if(playBoard[i + k - 1][j] == 0 && playBoard[i + k + 4][j] == 0) {
                                    superWeight[i + k - 1][j] += 500;
                                    superWeight[i + k + 4][j] += 500;
                                    return;
                                }
                                else if(playBoard[i - 1][j] == color && playBoard[i + k + 4][j] == 0) {
                                    superWeight[i + k + 4][j] += 500;
                                    return;
                                } 
                                else if(playBoard[i + 4][j ] == color && playBoard[i + k - 1][j] == 0) {
                                    superWeight[i + k - 1][j ] += 500;
                                    return;
                                }
                                break;
                            }
                        }
                    }


                    else if(check == 3){
                        for(int k = 0; k < 6; k++) {
                            //3연속 1 공백 1
                            if(playBoard[i + k][j] == opponent && playBoard[i + k + 4][j ] == opponent) {
                                if(playBoard[i + k - 1][j ] == 0 && playBoard[i + k + 5][j ] == 0) {
                                    superWeight[i + k - 1][j ] += 500;
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i + k - 1][j ] == color) {
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i + k + 5][j ] == color){
                                    superWeight[i + k - 1][j ] += 500;
                                    return;
                                }
                                break;
                            }
                            //3연속 2공백 1
                            else if(playBoard[i + k][j] == opponent && playBoard[i + k + 3][j ] == 0 && playBoard[i + k + 4][j ] == 0) {
                                 if (weight[i + k + 3][j] > weight[i + k + 4][j ]) {
                                     superWeight[i + k + 3][j ] += 500;    
                                 } else {
                                     superWeight[i + k + 4][j ] += 500;
                                 }
                                 return;
                            }
                        }
                    }


                    else if(check == 2) {
                        for(int k = 0; k < 6; k++) {
                            if(playBoard[i ][j + k] == opponent) {
                                //2연속 1공백 2
                                if(playBoard[i + k - 1][j ] == 0 &&playBoard[i + k + 5][j ] == 0) {
                                    superWeight[i + k - 1][j ] += 500;
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i + k -1][j ] == color) {
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i + k + 5][j ] == color) {
                                    superWeight[i + k - 1][j ] += 500;
                                    return;
                                }
                                //2연속 2공백 2
                                else if(playBoard[i + k + 2][j ] == 0 && playBoard[i + k + 3][j ] == 0){
                                    if(weight[i + k + 2][j ] > weight[i + k + 3][j ]) {
                                        superWeight[i + k + 2][j ] += 500;
                                    }
                                    else {
                                        superWeight[i + k + 3][j ] += 500;
                                    }
                                    return;
                                }
                                break;
                            }
                        }
                    }


                    else if(check == 1) {
                        for(int k = 0; k < 6; k++) {
                            //1 1공백 3연속
                            if(playBoard[i + k][j ] == opponent && playBoard[i + k + 2][j ] == opponent) {
                                if(playBoard[i + k - 1][j ] == 0 && playBoard[i + k + 5][j ] == 0) {
                                    superWeight[i + k - 1][j ] += 500;
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i + k - 1][j ] == color) {
                                    superWeight[i + k + 5][j ] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i + k + 5][j ] == color) {
                                    superWeight[i + k - 1][j ] += 500;
                                    return;
                                }
                                break;
                            }
                            //1 2공백 3연속
                            else if(playBoard[i + k][j ] == opponent) {
                                if(weight[i + k + 1][j ] > weight[i + k + 2][j ]) {
                                    superWeight[i + k + 1][j ] += 500;
                                }else {
                                    superWeight[i + k + 2][j ] += 500;
                                }
                                return;
                            }
                        } 
                    } 
                    
                } catch (ArrayIndexOutOfBoundsException e) { }
              }




          }
      }
      
      
      
      
      //세로 방어 시작점 ------------------------------------------------------

      
      for (int i = 0; i < 19; i++) {
          for (int j = 0; j < 19; j++) { //14넘으면 여섯개 범위 잡을 때 어차피 인덱스 에러 나니깐... 
              myCount = 0;
              check = 0;
              //본인기준 여섯개 체크 
              for (int k = 0; k < 6; k++) {
                  try {
                      //상대 돌의 연속점? 세는거같은데 중간에 자기 색이 나오면 세던거 엎어버림. 
                      if (playBoard[i][j + k] == opponent) {
                          myCount++;
                      }
                      else if(playBoard[i][j + k] == color) {
                          myCount = 0;
                          break;
                      }
                      //비어있는부분 만나면 체크를.. 그 비어있는곳 앞에 몇개의 상대돌이 연속되어있는지 저장해주는건가봐. 
                      else if(playBoard[i][j + k] == 0) {
                          if(check == 0 &&  myCount != 0)
                              check = myCount;
                      }
                      
                  } catch (ArrayIndexOutOfBoundsException e) { }
              }


           if(myCount == 5) {
                  try {
                      //연속
                      if(check == 5) {
                          if(playBoard[i][j] == 0 && playBoard[i][j - 1] != opponent) {
                              if(playBoard[i][j + 6] == 0) {
                                  superWeight[i][j] += 500;
                                  superWeight[i][j + 6] += 500;
                                  return;
                              }
                              else if(playBoard[i][j + 6] == color) {
                                  superWeight[i][j] += 500; 
                                  return;
                              }
                          }
                          else if(playBoard[i][j + 5] == 0 && playBoard[i][j - 6] != opponent) {
                              if(playBoard[i][j - 1] == 0) {
                                  superWeight[i][j - 1] += 500;
                                  superWeight[i][j + 5] += 500; 
                                  return;
                              }
                              else if(playBoard[i][j - 1] == color) {
                                  superWeight[i][j + 5] += 500; 
                                  return;
                              }
                          }
                      }


                      //중간에 공백 있음
                      else if(playBoard[i][j - 1] != opponent && playBoard[i][j + 6] != opponent){
                          for(int k = 0; k < 6; k++) {
                              if(playBoard[i][j + k] == 0) {
                                  superWeight[i][j + k] += 500;
                                  return;
                              }
                          }
                      }


                  }
                  catch (ArrayIndexOutOfBoundsException e) { }
              }


              //4연
              else if(myCount == 4) {
                try {

                    if(check == 4) {
                        for(int k = 0; k < 6; k++) {
                            //4개 연속
                            if(playBoard[i ][j + k] == opponent) {
                                if(playBoard[i ][j + k - 1] == 0 && playBoard[i ][j + k + 4] == 0) {
                                    superWeight[i ][j + k - 1] += 500;
                                    superWeight[i ][j + k + 4] += 500;
                                    return;
                                }
                                else if(playBoard[i ][j - 1] == color && playBoard[i ][j + k + 4] == 0) {
                                    superWeight[i ][j + k + 4] += 500;
                                    return;
                                } 
                                else if(playBoard[i ][j + 4] == color && playBoard[i ][j + k - 1] == 0) {
                                    superWeight[i ][j + k - 1] += 500;
                                    return;
                                }
                                break;
                            }
                        }
                    }


                    else if(check == 3){
                        for(int k = 0; k < 6; k++) {
                            //3연속 1 공백 1
                            if(playBoard[i ][j + k] == opponent && playBoard[i ][j + k + 4] == opponent) {
                                if(playBoard[i ][j + k - 1] == 0 && playBoard[i ][j + k + 5] == 0) {
                                    superWeight[i ][j + k - 1] += 500;
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i ][j + k - 1] == color) {
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i ][j + k + 5] == color){
                                    superWeight[i ][j + k - 1] += 500;
                                    return;
                                }
                                break;
                            }
                            //3연속 2공백 1
                            else if(playBoard[i ][j + k] == opponent && playBoard[i ][j + k + 3] == 0 && playBoard[i ][j + k + 4] == 0) {
                                 if (weight[i ][j + k + 3] > weight[i ][j + k + 4]) {
                                     superWeight[i ][j + k + 3] += 500;    
                                 } else {
                                     superWeight[i ][j + k + 4] += 500;
                                 }
                                 return;
                            }
                        }
                    }


                    else if(check == 2) {
                        for(int k = 0; k < 6; k++) {
                            if(playBoard[i ][j + k] == opponent) {
                                //2연속 1공백 2
                                if(playBoard[i ][j + k - 1] == 0 &&playBoard[i ][j + k + 5] == 0) {
                                    superWeight[i ][j + k - 1] += 500;
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i ][j + k -1] == color) {
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i ][j + k + 5] == color) {
                                    superWeight[i ][j + k - 1] += 500;
                                    return;
                                }
                                //2연속 2공백 2
                                else if(playBoard[i ][j + k + 2] == 0 && playBoard[i ][j + k + 3] == 0){
                                    if(weight[i ][j + k + 2] > weight[i ][j + k + 3]) {
                                        superWeight[i ][j + k + 2] += 500;
                                    }
                                    else {
                                        superWeight[i ][j + k + 3] += 500;
                                    }
                                    return;
                                }
                                break;
                            }
                        }
                    }


                    else if(check == 1) {
                        for(int k = 0; k < 6; k++) {
                            //1 1공백 3연속
                            if(playBoard[i ][j + k] == opponent && playBoard[i ][j + k + 2] == opponent) {
                                if(playBoard[i ][j + k - 1] == 0 && playBoard[i ][j + k + 5] == 0) {
                                    superWeight[i ][j + k - 1] += 500;
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 0 && playBoard[i ][j + k - 1] == color) {
                                    superWeight[i ][j + k + 5] += 500;
                                    return;
                                }
                                else if(k == 1 && playBoard[i ][j + k + 5] == color) {
                                    superWeight[i ][j + k - 1] += 500;
                                    return;
                                }
                                break;
                            }
                            //1 2공백 3연속
                            else if(playBoard[i ][j + k] == opponent) {
                                if(weight[i ][j + k + 1] > weight[i ][j + k + 2]) {
                                    superWeight[i ][j + k + 1] += 500;
                                }else {
                                    superWeight[i ][j + k + 2] += 500;
                                }
                                return;
                            }
                        } 
                    } 
                    
                } catch (ArrayIndexOutOfBoundsException e) { }
              }




          }
      }
      
     

      
      
      
      
      //좌대각)\) 방어 시작점 ------------------------------------------------------------------------
      
      for (int i = 0; i < 19; i++) {
          for (int j = 0; j < 19; j++) {
              myCount = 0;
              check = 0;
              for (int k = 0; k < 6; k++) {
                  try {
                      if (playBoard[i + k][j + k] == opponent) {
                          myCount++;
                      } else if (playBoard[i + k][j + k] == color) {
                          myCount = 0;
                          break;
                      } else if (playBoard[i + k][j + k] == 0) {
                          if (check == 0 && myCount != 0)
                              check = myCount;
                      }

                  } catch (ArrayIndexOutOfBoundsException e) {
                  }
              }
              // 5 왼쪽 위에서 오른쪽 아래(좌대각\) 방어
              if (myCount == 5) {
                  try {
                      // 연속
                      if (check == 5) {
                          try {
                              if (playBoard[i][j] == 0 && playBoard[i - 1][j - 1] != opponent) {
                                  if (playBoard[i + 6][j + 6] == 0) {
                                      superWeight[i][j] += 500;
                                      superWeight[i + 6][j + 6] += 500;
                                      return;
                                  } else if (playBoard[i + 6][j + 6] == color) {
                                      superWeight[i][j] += 500;
                                      return;
                                  }
                              } else if (playBoard[i + 5][j + 5] == 0 && playBoard[i - 6][j - 6] != opponent) {
                                  if (playBoard[i - 1][j - 1] == 0) {
                                      superWeight[i - 1][j - 1] += 500;
                                      superWeight[i + 5][j + 5] += 500;
                                      return;
                                  } else if (playBoard[i - 1][j - 1] == color) {
                                      superWeight[i + 5][j + 5] += 500;
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                      // 중간에 공백 있음
                      else if (playBoard[i - 1][j - 1] != opponent && playBoard[i + 6][j + 6] != opponent) {
                          for (int k = 0; k < 6; k++) {
                              if (playBoard[i + k][j + k] == 0) {
                                  superWeight[i + k][j + k] += 500;
                                  return;
                              }
                          }
                      }
                  } catch (ArrayIndexOutOfBoundsException e) {
                  }
              } else if (myCount == 4) {
                  if (check == 4) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 4개 연속
                              if (playBoard[i + k][j + k] == opponent) {
                                  if (playBoard[i + k - 1][j + k - 1] == 0
                                          && playBoard[i + k + 4][j + k + 4] == 0) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      superWeight[i + k + 4][j + k + 4] += 500;
                                      return;
                                  } else if (playBoard[i - 1][j - 1] == color
                                          && playBoard[i + k + 4][j + k + 4] == 0) {
                                      superWeight[i + k + 4][j + k + 4] += 500;
                                      return;
                                  } else if (playBoard[i + 4][j + 4] == color
                                          && playBoard[i + k - 1][j + k - 1] == 0) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 3) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 3연속 1 공백 1
                              if (playBoard[i + k][j + k] == opponent
                                      && playBoard[i + k + 4][j + k + 4] == opponent) {
                                  if (playBoard[i + k - 1][j + k - 1] == 0 && playBoard[i + k + 5][j + k + 5] == 0) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i + k - 1][j + k - 1] == color) {
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i + k + 5][j + k + 5] == color) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                              // 3연속 2공백 1
                              else if (playBoard[i + k][j + k] == opponent && playBoard[i + k + 3][j + k + 3] == 0
                                      && playBoard[i + k + 4][j + k + 4] == 0) {
                                  if (weight[i + k + 3][j + k + 3] > weight[i + k + 4][j + k + 4]) {
                                      superWeight[i + k + 3][j + k + 3] += 500;
                                  } else {
                                      superWeight[i + k + 4][j + k + 4] += 500;
                                  }
                                  return;
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 2) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              if (playBoard[i + k][j + k] == opponent) {
                                  // 2연속 1공백 2
                                  if (playBoard[i + k - 1][j + k - 1] == 0 && playBoard[i + k + 5][j + k + 5] == 0) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i + k - 1][j + k - 1] == color) {
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i + k + 5][j + k + 5] == color) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      return;
                                  }
                                  // 2연속 2공백 2
                                  else if (playBoard[i + k + 2][j + k + 2] == 0
                                          && playBoard[i + k + 3][j + k + 3] == 0) {
                                      if (weight[i + k + 2][j + k + 2] > weight[i + k + 3][j + k + 3]) {
                                          superWeight[i + k + 2][j + k + 2] += 500;
                                      } else {
                                          superWeight[i + k + 3][j + k + 3] += 500;
                                      }
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 1) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 1 1공백 3연속
                              if (playBoard[i + k][j + k] == opponent
                                      && playBoard[i + k + 2][j + k + 2] == opponent) {
                                  if (playBoard[i + k - 1][j + k - 1] == 0 && playBoard[i + k + 5][j + k + 5] == 0) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i + k - 1][j + k - 1] == color) {
                                      superWeight[i + k + 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i + k + 5][j + k + 5] == color) {
                                      superWeight[i + k - 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                              // 1 2공백 3연속
                              else if (playBoard[i + k][j + k] == opponent) {
                                  if (weight[i + k + 1][j + k + 1] > weight[i + k + 2][j + k + 2]) {
                                      superWeight[i + k + 1][j + k + 1] += 500;
                                  } else {
                                      superWeight[i + k + 2][j + k + 2] += 500;
                                  }
                                  return;
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  }
              }
          }
      }

      // 우대각 방어 시작점 -------------------------------------------------------------------------------
      
      for (int j = 0; j < 19; j++) {
          for (int i = 5; i < 19; i++) {
              myCount = 0;
              check = 0;
              for (int k = 0; k < 6; k++) {
                  try {
                      if (playBoard[i - k][j + k] == opponent) {
                          myCount++;
                      } else if (playBoard[i - k][j + k] == color) {
                          myCount = 0;
                          break;
                      } else if (playBoard[i - k][j + k] == 0) {
                          if (check == 0 && myCount != 0)
                              check = myCount;
                      }

                  } catch (ArrayIndexOutOfBoundsException e) {
                  }
              }
              if (myCount == 5) {
                  try {
                      // 5개 연속
                      if (check == 5) {
                          try {
                              if (playBoard[i][j] == 0 && playBoard[i + 1][j - 1] != opponent) {
                                  if (playBoard[i - 6][j + 6] == 0) {
                                      superWeight[i][j] += 500;
                                      superWeight[i - 6][j + 6] += 500;
                                      return;
                                  } else if (playBoard[i - 6][j + 6] == color) {
                                      superWeight[i][j] += 500;
                                      return;
                                  }
                              } else if (playBoard[i - 5][j + 5] == 0 && playBoard[i - 6][j + 6] != opponent) {
                                  if (playBoard[i - 1][j + 1] == 0) {
                                      superWeight[i - 1][j + 1] += 500;
                                      superWeight[i - 5][j + 5] += 500;
                                      return;
                                  } else if (playBoard[i + 1][j - 1] == color) {
                                      superWeight[i - 5][j + 5] += 500;
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                      // 중간에 공백 있음
                      else if (playBoard[i + 1][j - 1] != opponent && playBoard[i - 6][j + 6] != opponent) {
                          for (int k = 0; k < 6; k++) {
                              if (playBoard[i - k][j + k] == 0) {
                                  superWeight[i - k][j + k] += 500;
                                  return;
                              }
                          }
                      }
                  } catch (ArrayIndexOutOfBoundsException e) {
                  }
              } else if (myCount == 4) {
                  if (check == 4) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 4개 연속
                              if (playBoard[i - k][j + k] == opponent) {
                                  if (playBoard[i - k + 1][j + k - 1] == 0 && playBoard[i - k - 4][j + k + 4] == 0) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      superWeight[i - k - 4][j + k + 4] += 500;
                                      return;
                                  } else if (playBoard[i + 1][j - 1] == color
                                          && playBoard[i - k - 4][j + k + 4] == 0) {
                                      superWeight[i - k - 4][j + k + 4] += 500;
                                      return;
                                  } else if (playBoard[i - 4][j + 4] == color
                                          && playBoard[i - k + 1][j + k - 1] == 0) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 3) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 3연속 1 공백 1
                              if (playBoard[i - k][j + k] == opponent
                                      && playBoard[i - k - 4][j + k + 4] == opponent) {
                                  if (playBoard[i - k + 1][j + k - 1] == 0 && playBoard[i - k - 5][j + k + 5] == 0) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i - k + 1][j + k - 1] == color) {
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i - k - 5][j + k + 5] == color) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                              // 3연속 2공백 1
                              else if (playBoard[i - k][j + k] == opponent && playBoard[i - k - 3][j + k + 3] == 0
                                      && playBoard[i - k - 4][j + k + 4] == 0) {
                                  if (weight[i - k - 3][j + k + 3] > weight[i - k - 4][j + k + 4]) {
                                      superWeight[i - k - 3][j + k + 3] += 500;
                                  } else {
                                      superWeight[i - k - 4][j + k + 4] += 500;
                                  }
                                  return;
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 2) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              if (playBoard[i - k][j + k] == opponent) {
                                  // 2연속 1공백 2
                                  if (playBoard[i - k + 1][j + k - 1] == 0 && playBoard[i - k - 5][j + k + 5] == 0) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i - k + 1][j + k - 1] == color) {
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i - k - 5][j + k + 5] == color) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      return;
                                  }
                                  // 2연속 2공백 2
                                  else if (playBoard[i - k - 2][j + k + 2] == 0
                                          && playBoard[i - k - 3][j + k + 3] == 0) {
                                      if (weight[i - k - 2][j + k + 2] > weight[i - k - 3][j + k + 3]) {
                                          superWeight[i - k - 2][j + k + 2] += 500;
                                      } else {
                                          superWeight[i - k + 3][j - k + 3] += 500;
                                      }
                                      return;
                                  }
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  } else if (check == 1) {
                      for (int k = 0; k < 6; k++) {
                          try {
                              // 1 1공백 3연속
                              if (playBoard[i - k][j + k] == opponent
                                      && playBoard[i - k - 2][j + k + 2] == opponent) {
                                  if (playBoard[i - k + 1][j + k - 1] == 0 && playBoard[i - k - 5][j + k + 5] == 0) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 0 && playBoard[i - k + 1][j + k - 1] == color) {
                                      superWeight[i - k - 5][j + k + 5] += 500;
                                      return;
                                  } else if (k == 1 && playBoard[i - k - 5][j + k + 5] == color) {
                                      superWeight[i - k + 1][j + k - 1] += 500;
                                      return;
                                  }
                              }
                              // 1 2공백 3연속
                              else if (playBoard[i - k][j + k] == opponent) {
                                  if (weight[i - k - 1][j + k + 1] > weight[i - k - 2][j + k + 2]) {
                                      superWeight[i - k - 1][j + k + 1] += 500;
                                  } else {
                                      superWeight[i - k - 2][j + k + 2] += 500;
                                  }
                                  return;
                              }
                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  }
              }
          }
      }
      
      for(int i = 0; i < 19; i++) {
         for(int j = 0; j < 19; j++) {
            myCount = 0;
            int k = j;
            while(myCount < 6) {
               if(playBoard[i][k]  == color) {
                  myCount++;
                  k++;
               } else
                  break;
            }
            if(myCount > 1) {
              superWeight[i][j - 1] += myCount * 10;
              superWeight[i][k] += myCount * 10;
            }
         }
      }
      
      for(int i = 0; i < 19; i++) {
         for(int j = 0; j < 19; j++) {
            myCount = 0;
            int k = j;
            while(myCount < 6) {
               if(playBoard[k][i]  == color) {
                  myCount++;
                  k++;
               } else
                  break;
            }
            if(myCount > 1) {
              superWeight[i][j - 1] += myCount * 10;
              superWeight[i][k] += myCount * 10;
            }
         }
      }
      
      for(int i = 0; i < 19; i++) {
         for(int j = 0; j < 19; j++) {
            myCount = 0;
            int m = i;
            int n = j;
            while(myCount < 6) {
               if(playBoard[m][n]  == color) {
                  myCount++;
                  m++;
                  n++;
               } else
                  break;
            }
            if(myCount > 1) {
              superWeight[i - 1][j - 1] += myCount * 10;
              superWeight[m][n] += myCount * 10;
            }
         }
      }

  }

      //// 전개 플러스점수
      //6개 범위 내에서 나의/상대의 돌 갯수와 빈칸의 합이 6이라면 빈칸에 나의/상대의 돌 *10만큼 가중치 주기. 

  

  
  
  // 일반가중치+특수가중치 판에서 최대 가중치를 찾아 x,y 값 저장해주기
  public static void returnPoint() {
     
     addSuperWeight();
     
      int max = 0;
      for (int i = 0; i < 19; i++) {
          for (int j = 0; j < 19; j++) {
              if (superWeight[i][j] + weight[i][j] > max) {
                  max = superWeight[i][j] + weight[i][j];
                  x = i;
                  y = j;
              }
          }
      }
      
//      showWeight();

  }

  
  
  // 현재 가중치 상태 콘솔에 출력
  public static void showWeight() {
      for (int i = 18; i >= 0; i--) {
          for (int j = 0; j < 19; j++) {
              System.out.printf("[%6d]", weight[j][i]);
          }
          System.out.println("");
      }
      System.out.println("");
  }

  

}