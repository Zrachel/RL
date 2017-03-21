# Description of each file

## MountainCar

### SARS
State: (x, v) in which x:position, v:velocity <br/>
Action: backwards, forwards, coast <br/>
Terminal: x>=0.5, i.e., at the most right side <br/>
Reward: 100 at Terminal, 0 else

### Result:

```
physParams:
===========================
xmin:-1.2, xmax:0.5, vallypos:-0.5235987755982988
vmin:-0.07, vmax:0.07
===========================
s: x:-0.6940435564543731, v:0.04768356302828289, action:forward, reward:0.0
s': x:-0.6441366400458586, v:0.049906916408514496
===========================
Finished iteration: 0. Weight change: Infinity
Finished iteration: 1. Weight change: 568.3746173209856
Finished iteration: 2. Weight change: 315.2019060063935
Finished iteration: 3. Weight change: 52.4193641348874
Finished iteration: 4. Weight change: 1.234166790135695
Finished iteration: 5. Weight change: 0.004166549229728981
Finished iteration: 6. Weight change: 0.0
Finished Policy Iteration.
===========================
State process according to policy:

State 0:	x = -0.5235987755982988;	v = 0.0;	action = backwards
State 10:	x = -0.5749846237076911;	v = -0.008808011932669587;	action = backwards
State 20:	x = -0.684572011782047;	v = -0.011542340010727334;	action = backwards
State 30:	x = -0.7799300198795487;	v = -0.0069914603222286;	action = backwards
State 40:	x = -0.8052213656581466;	v = 0.001351057580191769;	action = backwards
State 50:	x = -0.7362777805466612;	v = 0.013949805020554717;	action = forward
State 60:	x = -0.4826858072114902;	v = 0.03208528992716805;	action = forward
State 70:	x = -0.16112381150146712;	v = 0.02917904292406543;	action = forward
State 80:	x = 0.054660982956677866;	v = 0.014939633075632695;	action = forward
State 90:	x = 0.1264649063351034;	v = 0.001076815404845935;	action = backwards
State 100:	x = -0.04550372762813647;	v = -0.031035261163506838;	action = coast
State 110:	x = -0.47911568363670426;	v = -0.0534645441877352;	action = forward
State 120:	x = -0.9192443068206888;	v = -0.03197253510077419;	action = forward
State 130:	x = -1.0500864565271604;	v = 0.0026252404082252115;	action = forward
State 140:	x = -0.8333093975085284;	v = 0.036752071625737404;	action = forward
State 150:	x = -0.34504005527728854;	v = 0.05373632899836603;	action = forward
State 160:	x = 0.13960930891902926;	v = 0.042297215017051415;	action = forward
```

## 
