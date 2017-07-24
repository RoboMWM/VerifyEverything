package to.us.tf.verifyeverything;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 7/24/2017.
 *
 * @author RoboMWM
 */
public class VerifyEverything extends JavaPlugin
{
    boolean enabled = false;
    private Set<Listener> listeners = new HashSet<>();

    public void onEnable()
    {
        listeners.add(new PickupListener(this));
    }

    private void registerListeners()
    {
        for (Listener listener : listeners)
            getServer().getPluginManager().registerEvents(listener, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!sender.isOp())
            return false;

        if (enabled)
        {
            HandlerList.unregisterAll(this);
            sender.sendMessage("Unregistered all listeners");
        }
        else
        {
            registerListeners();
            sender.sendMessage("Registered listeners");
        }

        return true;

    }
}
