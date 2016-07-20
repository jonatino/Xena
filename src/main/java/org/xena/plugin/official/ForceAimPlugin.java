package org.xena.plugin.official;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;
import org.xena.utils.Utils;

import java.util.Arrays;

import static com.sun.jna.platform.win32.WinUser.SM_CXSCREEN;
import static com.sun.jna.platform.win32.WinUser.SM_CYSCREEN;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class ForceAimPlugin extends Plugin {

    private final AngleUtils aimHelper;

    public ForceAimPlugin(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

	private float[] aim = new float[3];
	private final float[] lastaim = new float[3];

    private Player lastTarget = null;

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> players) {
        if (NativeKeyUtils.isLeftAltDown()) {

            Player target = me.getTarget();
            if (lastTarget != null && target == null) {
                if (!lastTarget.isDead() && lastTarget.isSpotted()) {
                    target = lastTarget;
                } else {
                    lastTarget = null;
                }
            }

            if (target == null) {
                return;
            }

            System.out.println(target + ", " + target.address());

            if (aimHelper.canShoot(me, target)) {
                aimHelper.velocityComp(me, target, target.getBones());
                aimHelper.calculateAngle(me, me.getPosition(), target.getBones(), aim);
	            //aimHelper.setAngleSmooth(aim, target.getViewAngles());

	            float[] nan = new float[3];
	            Utils.worldToScreen(aim, nan);
	            MouseMove(nan[0], nan[1]);
	            lastaim[0] = aim[0];
	            lastaim[1] = aim[1];

	            lastTarget = target;
            } else {
                lastTarget = null;
            }
        } else {
            lastTarget = null;
        }
    }

	public static final long MOUSEEVENTF_MOVE = 0x0001L;
	public static final long MOUSEEVENTF_ABSOLUTE = 0x8000L;

	public static WinDef.RECT getRect(String windowName) {
		WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new RuntimeException(windowName);
		}

		WinDef.RECT rect = new WinDef.RECT();
		boolean result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (!result) {
			throw new RuntimeException(windowName);
		}
		return rect;
	}

	public static void MouseMove(float delta_x, float delta_y) {
		int mouse_move_x = (int) delta_x;
		int mouse_move_y = (int) delta_y;
		double fScreenWidth = User32.INSTANCE.GetSystemMetrics(SM_CXSCREEN) - 1;
		double fScreenHeight = User32.INSTANCE.GetSystemMetrics(SM_CYSCREEN) - 1;
		double dx = 65535.0f / fScreenWidth;
		double dy = 65535.0f / fScreenHeight;
		WinDef.RECT rect = getRect("Counter-Strike: Global Offensive");//TODO fix this dumb shit rofl
		int[] mouse_center = new int[2];
		int[] wndpos = new int[2];

		wndpos[0] = rect.left;
		wndpos[1] = rect.top;

		mouse_center[0] = (rect.right - rect.left) / 2;
		mouse_center[1] = (rect.bottom - rect.top) / 2;

		double fx = (wndpos[0] + mouse_center[0] + delta_x) * dx;
		double fy = (wndpos[1] + mouse_center[1] + delta_y) * dy;
		WinUser.INPUT input = new WinUser.INPUT();
		input.input.setType("mi");
		input.input.mi.dwFlags = new WinDef.DWORD(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE);
		input.input.mi.dx = new WinDef.LONG((long) fx);
		input.input.mi.dy = new WinDef.LONG((long) fy);

		System.out.println(fx + ", " + fy);
		System.out.println(mouse_move_x + ", " + mouse_move_y);
		System.out.println(Arrays.toString(wndpos));
		System.out.println(Arrays.toString(mouse_center));

		WinUser.INPUT[] inArray = (WinUser.INPUT[]) input.toArray(1);
		User32.INSTANCE.SendInput(new WinDef.DWORD(1), inArray, input.size());
	}

}
