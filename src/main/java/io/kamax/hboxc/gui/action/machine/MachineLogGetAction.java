package io.kamax.hboxc.gui.action.machine;

import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.vm.MachineLogFileViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class MachineLogGetAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = -3163242452645805100L;
    private String _srvId;
    private String _vmId;

    public MachineLogGetAction(String srvId, String vmId) {
        super("View Log Files", IconBuilder.getTask(HypervisorTasks.MachineLogFileGet));
        _srvId = srvId;
        _vmId = vmId;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        MachineLogFileViewer.show(_srvId, _vmId);
    }

}
